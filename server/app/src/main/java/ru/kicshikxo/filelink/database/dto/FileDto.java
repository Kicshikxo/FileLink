package ru.kicshikxo.filelink.database.dto;

import java.sql.Timestamp;
import java.util.UUID;

public class FileDto {
  private UUID fileId;
  private UUID userId;
  private String fileName;
  private long fileSize;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;
  private Timestamp expiredAt;

  public FileDto() {
  }

  public FileDto(UUID fileId, UUID userId, String name, long size,
      Timestamp createdAt, Timestamp updatedAt,
      Timestamp deletedAt, Timestamp expiredAt) {
    this.fileId = fileId;
    this.userId = userId;
    this.fileName = name;
    this.fileSize = size;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
    this.expiredAt = expiredAt;
  }

  public UUID getFileId() {
    return fileId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getFileName() {
    return fileName;
  }

  public long getFileSize() {
    return fileSize;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public Timestamp getDeletedAt() {
    return deletedAt;
  }

  public Timestamp getExpiredAt() {
    return expiredAt;
  }
}
