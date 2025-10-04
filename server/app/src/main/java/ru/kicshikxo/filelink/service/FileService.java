package ru.kicshikxo.filelink.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import ru.kicshikxo.filelink.database.repository.FileRepository;
import ru.kicshikxo.filelink.dto.file.FileDto;

public class FileService {
  private static final Dotenv dotenv = Dotenv.load();

  private static final String UPLOADS_DIRECTORY = dotenv.get("UPLOADS_DIRECTORY", "uploads");

  private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;

  public File getFile(UUID fileId) throws SQLException {
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

    return matchingFiles[0];
  }

  public List<FileDto> getFiles(UUID userId) throws SQLException {
    return FileRepository.getByUserId(userId);
  }

  public List<FileDto> saveFiles(UUID userId, List<UploadedFile> uploadedFiles) throws SQLException {
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

    List<FileDto> results = new ArrayList<>();
    for (UploadedFile uploadedFile : uploadedFiles) {
      UUID fileUuid = UUID.randomUUID();
      String extension = getFileExtension(uploadedFile.filename());
      File savedFile = new File(UPLOADS_DIRECTORY, fileUuid + extension);

      FileUtil.streamToFile(uploadedFile.content(), savedFile.toString());
      FileRepository.createWithId(fileUuid, userId, uploadedFile.filename(), uploadedFile.size());

      results.add(FileRepository.getById(fileUuid));
    }
    return results;
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return dotIndex != -1 ? filename.substring(dotIndex) : "";
  }
}
