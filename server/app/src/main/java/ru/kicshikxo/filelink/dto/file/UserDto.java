package ru.kicshikxo.filelink.dto.file;

import java.sql.Timestamp;
import java.util.UUID;

public class UserDto {
  private UUID userId;
  private String email;
  private String passwordHash;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  public UserDto() {
  }

  public UserDto(UUID userId, String email, String passwordHash, Timestamp createdAt, Timestamp updatedAt) {
    this.userId = userId;
    this.email = email;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }
}
