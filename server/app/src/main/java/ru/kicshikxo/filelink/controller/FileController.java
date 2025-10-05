package ru.kicshikxo.filelink.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.attachment.Rfc5987Util;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import io.javalin.http.util.NaiveRateLimit;
import ru.kicshikxo.filelink.database.repository.FileDownloadsRepository;
import ru.kicshikxo.filelink.database.repository.FileRepository;
import ru.kicshikxo.filelink.dto.file.FileDto;
import ru.kicshikxo.filelink.middleware.AuthMiddleware;
import ru.kicshikxo.filelink.service.FileService;

public class FileController {
  private final FileService fileService = new FileService();

  public void registerRoutes(Javalin app) {
    app.before("/api/files/list", new AuthMiddleware()::handle);
    app.before("/api/files/upload", new AuthMiddleware()::handle);
    app.before("/api/files/delete/{fileId}", new AuthMiddleware()::handle);
    app.before("/api/files/statistics/{fileId}", new AuthMiddleware()::handle);
    app.before("/api/files/download/{fileId}", new AuthMiddleware(false)::handle);

    app.get("/api/files/list", this::list);
    app.post("/api/files/upload", this::upload);
    app.delete("/api/files/delete/{fileId}", this::delete);
    app.get("/api/files/statistics/{fileId}", this::statistics);
    app.get("/api/files/download/{fileId}", this::download);
  }

  private void upload(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);

    try {
      UUID userId = ctx.attribute("userId");
      List<UploadedFile> uploadedFiles = ctx.uploadedFiles();
      ctx.json(fileService.saveFiles(userId, uploadedFiles));
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void list(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

    try {
      UUID userId = ctx.attribute("userId");
      List<FileDto> files = fileService.getFiles(userId);

      ctx.json(files);
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void delete(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

    try {
      UUID userId = ctx.attribute("userId");
      UUID fileId = UUID.fromString(ctx.pathParam("fileId"));

      FileDto file = FileRepository.getById(fileId);
      if (file == null) {
        throw new NotFoundResponse("FILE NOT FOUND");
      }

      if (!file.getUserId().equals(userId)) {
        throw new NotFoundResponse("FILE NOT FOUND");
      }

      fileService.deleteFile(fileId);
      ctx.json(Map.of("success", true));
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void statistics(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

    try {
      UUID userId = ctx.attribute("userId");
      UUID fileId = UUID.fromString(ctx.pathParam("fileId"));
      int days = ctx.queryParam("days") != null ? Integer.parseInt(ctx.queryParam("days")) : 7;

      FileDto file = FileRepository.getById(fileId);
      if (file == null) {
        throw new NotFoundResponse("FILE NOT FOUND");
      }

      if (!file.getUserId().equals(userId)) {
        throw new NotFoundResponse("FILE NOT FOUND");
      }

      ctx.json(Map.of(
          "file", file,
          "data", fileService.getFileStatistics(fileId, days)));
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void download(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

    try {
      UUID userId = ctx.attribute("userId");
      UUID fileId = UUID.fromString(ctx.pathParam("fileId"));

      FileDto fileDto = FileRepository.getById(fileId);
      if (fileDto == null) {
        throw new NotFoundResponse("FILE NOT FOUND");
      }

      if (fileDto.getDeletedAt() != null) {
        throw new NotFoundResponse("FILE DELETED");
      }
      if (fileDto.getExpiredAt() != null && fileDto.getExpiredAt().before(new Date())) {
        throw new NotFoundResponse("FILE EXPIRED");
      }

      File savedFile = fileService.getFile(fileId);

      String contentType = Files.probeContentType(savedFile.toPath());
      if (contentType == null) {
        contentType = "application/octet-stream";
      }
      ctx.contentType(contentType);

      String fileName = fileDto.getFileName().replace("\"", "\\\"");
      ctx.header("Content-Disposition", "inline; filename=\"" + fileName + "\"; filename*=UTF-8''"
          + Rfc5987Util.encode(fileName, "UTF-8"));

      ctx.result(new FileInputStream(savedFile));

      if (userId == null || !userId.equals(fileDto.getUserId())) {
        FileDownloadsRepository.createForFileById(fileId);
      }
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }
}
