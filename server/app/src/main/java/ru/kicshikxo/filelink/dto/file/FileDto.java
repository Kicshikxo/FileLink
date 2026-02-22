package ru.kicshikxo.filelink.dto.file;

import java.sql.Timestamp;
import java.util.UUID;

import ru.kicshikxo.filelink.util.ShortId;

public class FileDto {
  private UUID fileId;
  private UUID userId;
  private long fileIndex;
  private String fileShortId;
  private String fileName;
  private long fileSize;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;
  private Timestamp expiredAt;
  private Timestamp expiresAt;

  public FileDto() {
  }

  public FileDto(UUID fileId, UUID userId, long index, String name, long size, Timestamp createdAt, Timestamp updatedAt,
      Timestamp deletedAt, Timestamp expiredAt, Timestamp expiresAt) {
    this.fileId = fileId;
    this.userId = userId;
    this.fileIndex = index;
    this.fileShortId = ShortId.encode(index);
    this.fileName = name;
    this.fileSize = size;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
    this.expiredAt = expiredAt;
    this.expiresAt = expiresAt;
  }

  public UUID getFileId() {
    return fileId;
  }

  public UUID getUserId() {
    return userId;
  }

  public long getFileIndex() {
    return fileIndex;
  }

  public String getFileShortId() {
    return fileShortId;
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

  public Timestamp getExpiresAt() {
    return expiresAt;
  }
}
