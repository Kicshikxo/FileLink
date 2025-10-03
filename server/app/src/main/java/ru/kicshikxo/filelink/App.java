package ru.kicshikxo.filelink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.UploadedFile;
import io.javalin.http.util.NaiveRateLimit;
import ru.kicshikxo.filelink.auth.AuthMiddleware;
import ru.kicshikxo.filelink.auth.AuthService;
import ru.kicshikxo.filelink.auth.dto.LoginRequestDto;
import ru.kicshikxo.filelink.auth.utils.JwtUtils;
import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.database.dto.FileDto;
import ru.kicshikxo.filelink.database.repository.FileDownloadRepository;
import ru.kicshikxo.filelink.database.repository.FileRepository;

public class App {
  private static final Logger logger = LoggerFactory.getLogger(Database.class);

  private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;
  private static final String UPLOADS_DIRECTORY = "uploads";

  public static void main(String[] args) {
    initDatabase();

    Javalin app = Javalin.create(config -> {
      config.bundledPlugins.enableCors(cors -> {
        cors.addRule(it -> {
          it.anyHost();
        });
      });
    }).start(7070);

    app.before("/api/files/*", ctx -> {
      AuthMiddleware.handle(ctx, List.of("/api/files/download"));
    });
    app.post("/api/files/upload", ctx -> {
      NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);

      try {
        UUID userId = ctx.attribute("userId");

        List<UploadedFile> uploadedFiles = ctx.uploadedFiles();
        if (uploadedFiles.isEmpty()) {
          throw new BadRequestResponse("NO FILES UPLOADED");
        }

        for (UploadedFile file : uploadedFiles) {
          if (file.size() > MAX_FILE_SIZE) {
            throw new BadRequestResponse("File " + file.filename() + " exceeds the maximum size of 100 MB");
          }
        }

        File uploadsDirectory = new File(UPLOADS_DIRECTORY);
        if (!uploadsDirectory.exists()) {
          uploadsDirectory.mkdirs();
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (UploadedFile file : uploadedFiles) {
          UUID fileUuid = UUID.randomUUID();
          String extension = "";
          int dotIndex = file.filename().lastIndexOf('.');
          if (dotIndex != -1) {
            extension = file.filename().substring(dotIndex);
          }

          File savedFile = new java.io.File(uploadsDirectory, fileUuid + extension);

          try (InputStream inputStream = file.content();
              OutputStream outputStream = new FileOutputStream(savedFile)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, read);
            }
          }

          FileRepository.createWithId(fileUuid, userId, file.filename(), file.size());

          results.add(Map.of(
              "fileId", fileUuid.toString(),
              "fileName", file.filename(),
              "fileSize", file.size()));
        }

        ctx.json(results);
      } catch (Exception error) {
        throw new InternalServerErrorResponse(error.toString());
      }
    });
    app.get("/api/files/list", ctx -> {
      NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

      try {
        UUID userId = ctx.attribute("userId");
        List<FileDto> files = FileRepository.getByUserId(userId);

        ctx.json(files);
      } catch (SQLException error) {
        throw new InternalServerErrorResponse(error.toString());
      } catch (Exception error) {
        throw new InternalServerErrorResponse(error.toString());
      }
    });
    app.get("/api/files/download/{fileId}", ctx -> {
      NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

      try {
        UUID fileId = UUID.fromString(ctx.pathParam("fileId"));

        FileDto fileDto = FileRepository.getById(fileId);
        if (fileDto == null) {
          throw new NotFoundResponse("FILE NOT FOUND");
        }

        File uploadsDirectory = new File(UPLOADS_DIRECTORY);
        if (!uploadsDirectory.exists()) {
          throw new NotFoundResponse("UPLOADS DIRECTORY NOT FOUND");
        }

        File[] matchingFiles = uploadsDirectory.listFiles((dir, name) -> name.startsWith(fileId.toString()));
        if (matchingFiles == null || matchingFiles.length == 0) {
          FileRepository.deleteById(fileId);
          throw new NotFoundResponse("FILE NOT FOUND IN UPLOADS DIRECTORY");
        }

        File savedFile = matchingFiles[0];

        FileDownloadRepository.createForFileId(fileId);

        ctx.contentType("application/octet-stream");
        ctx.header("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + "\"");
        ctx.result(new FileInputStream(savedFile));
      } catch (IllegalArgumentException error) {
        throw new BadRequestResponse(error.toString());
      } catch (Exception error) {
        throw new InternalServerErrorResponse(error.toString());
      }
    });

    app.get("/api/auth/check", ctx -> {
      AuthMiddleware.handle(ctx);
    });
    app.post("/api/auth/login", ctx -> {
      NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

      try {
        LoginRequestDto loginRequest = ctx.bodyAsClass(LoginRequestDto.class);
        String token = AuthService.login(loginRequest.getEmail(), loginRequest.getPassword());

        ctx.cookie("filelink-token", token, JwtUtils.EXPIRATION_SECONDS);
        ctx.json(Map.of("token", token));
      } catch (RuntimeException error) {
        throw new UnauthorizedResponse(error.toString());
      } catch (Exception error) {
        throw new InternalServerErrorResponse(error.toString());
      }
    });

    app.exception(JsonProcessingException.class, (error, ctx) -> {
      throw new BadRequestResponse(error.toString());
    });
  }

  private static void initDatabase() {
    try {
      Database.update(
          "CREATE TABLE IF NOT EXISTS users (" +
              "user_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "email TEXT UNIQUE NOT NULL," +
              "\"password\" CHAR(60) NOT NULL," +
              "created_at TIMESTAMP DEFAULT NOW()," +
              "updated_at TIMESTAMP DEFAULT NOW()" +
              ")",
          null);

      Database.update(
          "CREATE TABLE IF NOT EXISTS files (" +
              "file_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "user_id uuid NOT NULL REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE," +
              "file_name TEXT NOT NULL," +
              "file_size BIGINT NOT NULL," +
              "created_at TIMESTAMP DEFAULT NOW()," +
              "updated_at TIMESTAMP DEFAULT NOW()," +
              "deleted_at TIMESTAMP DEFAULT NULL," +
              "expired_at TIMESTAMP DEFAULT NULL" +
              ")",
          null);

      Database.update(
          "CREATE INDEX IF NOT EXISTS index_files_user ON files(user_id)",
          null);

      Database.update(
          "CREATE TABLE IF NOT EXISTS file_downloads (" +
              "download_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "file_id uuid NOT NULL REFERENCES files(file_id) ON UPDATE CASCADE ON DELETE CASCADE," +
              "download_time TIMESTAMP DEFAULT NOW()" +
              ")",
          null);

      Database.update(
          "CREATE INDEX IF NOT EXISTS index_file_downloads_file ON file_downloads(file_id)",
          null);

      logger.info("Tables created or already exist");

    } catch (Exception error) {
      logger.error("Failed to create tables", error);
    }
  }
}
