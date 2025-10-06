package ru.kicshikxo.filelink.tasks;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kicshikxo.filelink.database.repository.FileRepository;
import ru.kicshikxo.filelink.dto.file.FileDto;
import ru.kicshikxo.filelink.service.FileService;

public class FileCleanupTask {
  private static final FileService fileService = new FileService();
  private static final Logger logger = LoggerFactory.getLogger(FileCleanupTask.class);

  public static void start() {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
        FileCleanupTask::cleanupOldFiles,
        0,
        5,
        TimeUnit.MINUTES);
  }

  private static void cleanupOldFiles() {
    try {
      List<FileDto> expiredFiles = FileRepository.getExpiredFiles();

      for (FileDto file : expiredFiles) {
        FileRepository.expireById(file.getFileId());

        File expiredFile = fileService.getFileById(file.getFileId());
        expiredFile.delete();
      }

      logger.info(expiredFiles.size() + " files marked as expired");
    } catch (Exception e) {
      logger.error("Error while cleaning files: " + e.toString());
    }
  }
}
