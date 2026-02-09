package ru.kicshikxo.filelink.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.javalin.config.SizeUnit;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import ru.kicshikxo.filelink.config.ServerConfig;
import ru.kicshikxo.filelink.database.repository.FileDownloadsRepository;
import ru.kicshikxo.filelink.database.repository.FileRepository;
import ru.kicshikxo.filelink.dto.file.DailyDownloadStatsDto;
import ru.kicshikxo.filelink.dto.file.FileDto;
import ru.kicshikxo.filelink.util.ShortId;

public class FileService {
  public FileDto getById(UUID fileId) throws SQLException {
    FileDto fileDto = FileRepository.getById(fileId);
    if (fileDto == null) {
      throw new NotFoundResponse("FILE NOT FOUND");
    }

    return fileDto;
  }

  public FileDto getByShortId(String shortId) throws SQLException {
    FileDto fileDto = FileRepository.getByIndex(ShortId.decode(shortId));
    if (fileDto == null) {
      throw new NotFoundResponse("FILE NOT FOUND");
    }

    return fileDto;
  }

  public File getFileById(UUID fileId) throws SQLException {
    File uploadsDirectory = new File(ServerConfig.UPLOADS_DIRECTORY);
    if (!uploadsDirectory.exists()) {
      throw new NotFoundResponse("UPLOADS DIRECTORY NOT FOUND");
    }

    File[] matchingFiles = uploadsDirectory.listFiles((directory, name) -> name.startsWith(fileId.toString()));
    if (matchingFiles == null || matchingFiles.length == 0) {
      FileRepository.deleteById(fileId);
      throw new NotFoundResponse("FILE NOT FOUND IN UPLOADS DIRECTORY");
    }

    return matchingFiles[0];
  }

  public List<FileDto> getFilesByUserId(UUID userId) throws SQLException {
    return FileRepository.getByUserId(userId);
  }

  public void renameFileById(UUID fileId, String newFileName) throws SQLException {
    FileDto fileDto = getById(fileId);

    if (fileDto.getDeletedAt() != null) {
      throw new NotFoundResponse("FILE DELETED");
    }
    if (fileDto.getExpiredAt() != null && fileDto.getExpiredAt().before(new Date())) {
      throw new NotFoundResponse("FILE EXPIRED");
    }

    String oldExtension = getFileExtension(fileDto.getFileName());
    String newExtension = getFileExtension(newFileName);

    if (newExtension.length() == 0) {
      newFileName = newFileName + oldExtension;
    }

    String extension = getFileExtension(newFileName);

    if (!extension.equals(oldExtension)) {
      File file = getFileById(fileId);
      file.renameTo(new File(file.getParent(), fileDto.getFileId() + extension));
    }

    FileRepository.renameById(fileId, newFileName);
  }

  public void deleteFileById(UUID fileId) throws SQLException {
    FileDto fileDto = getById(fileId);
    File file = getFileById(fileDto.getFileId());

    file.delete();
    FileRepository.deleteById(fileDto.getFileId());
  }

  public List<DailyDownloadStatsDto> getFileStatisticsById(UUID fileId, int days) throws SQLException {
    return FileDownloadsRepository.getFileDownloadStatisticsById(fileId, days);
  }

  public List<FileDto> saveFilesByUserId(UUID userId, List<UploadedFile> uploadedFiles) throws SQLException {
    if (uploadedFiles.isEmpty()) {
      throw new BadRequestResponse("NO FILES UPLOADED");
    }

    for (UploadedFile file : uploadedFiles) {
      if (file.size() > ServerConfig.MAX_FILE_SIZE_BYTES) {
        throw new BadRequestResponse("FILE " + file.filename() + " EXCEEDS THE MAXIMUM SIZE OF "
            + (ServerConfig.MAX_FILE_SIZE_BYTES / SizeUnit.MB.getMultiplier()) + " MB");
      }
    }

    long userFilesSize = FileRepository.getUserFilesSize(userId);
    long uploadedFilesSize = uploadedFiles.stream().mapToLong(UploadedFile::size).sum();

    if (userFilesSize + uploadedFilesSize > ServerConfig.MAX_USER_FILES_SIZE_BYTES) {
      throw new BadRequestResponse(
          "EXCEEDED " + (ServerConfig.MAX_USER_FILES_SIZE_BYTES / SizeUnit.MB.getMultiplier()) + " MB STORAGE LIMIT");
    }

    File uploadsDirectory = new File(ServerConfig.UPLOADS_DIRECTORY);
    if (!uploadsDirectory.exists()) {
      uploadsDirectory.mkdirs();
    }

    List<FileDto> results = new ArrayList<>();
    for (UploadedFile uploadedFile : uploadedFiles) {
      UUID fileUuid = UUID.randomUUID();
      String extension = getFileExtension(uploadedFile.filename());
      File savedFile = new File(ServerConfig.UPLOADS_DIRECTORY, fileUuid + extension);

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
