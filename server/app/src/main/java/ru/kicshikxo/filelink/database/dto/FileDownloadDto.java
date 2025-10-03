package ru.kicshikxo.filelink.database.dto;

import java.sql.Timestamp;
import java.util.UUID;

public class FileDownloadDto {
  private UUID downloadId;
  private UUID fileId;
  private Timestamp downloadTime;

  public FileDownloadDto() {
  }

  public FileDownloadDto(UUID downloadId, UUID fileId, Timestamp downloadTime) {
    this.downloadId = downloadId;
    this.fileId = fileId;
    this.downloadTime = downloadTime;
  }

  public UUID getDownloadId() {
    return downloadId;
  }

  public UUID getFileId() {
    return fileId;
  }

  public Timestamp getDownloadTime() {
    return downloadTime;
  }
}
