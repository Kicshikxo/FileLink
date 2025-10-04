package ru.kicshikxo.filelink.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.dto.file.FileDto;

public class FileRepository {
  public static FileDto getById(UUID fileId) throws SQLException {
    return Database.queryFirst(
        "SELECT * FROM files WHERE file_id = ?",
        preparedStatement -> preparedStatement.setObject(1, fileId),
        resultSet -> new FileDto(
            (UUID) resultSet.getObject("file_id"),
            (UUID) resultSet.getObject("user_id"),
            resultSet.getString("file_name"),
            resultSet.getLong("file_size"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("updated_at"),
            resultSet.getTimestamp("deleted_at") != null ? resultSet.getTimestamp("deleted_at")
                : null,
            resultSet.getTimestamp("expired_at") != null ? resultSet.getTimestamp("expired_at")
                : null));
  }

  public static void deleteById(UUID fileId) throws SQLException {
    Database.update(
        "DELETE FROM files WHERE file_id = ?",
        preparedStatement -> preparedStatement.setObject(1, fileId));
  }

  public static List<FileDto> getByUserId(UUID userId) throws SQLException {
    return Database.query(
        "SELECT * FROM files WHERE user_id = ?",
        preparedStatement -> preparedStatement.setObject(1, userId),
        resultSet -> new FileDto(
            (UUID) resultSet.getObject("file_id"),
            (UUID) resultSet.getObject("user_id"),
            resultSet.getString("file_name"),
            resultSet.getLong("file_size"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("updated_at"),
            resultSet.getTimestamp("deleted_at") != null ? resultSet.getTimestamp("deleted_at")
                : null,
            resultSet.getTimestamp("expired_at") != null ? resultSet.getTimestamp("expired_at")
                : null));
  }

  public static void createWithId(UUID fileId, UUID userId, String fileName, long fileSize) throws SQLException {
    Database.update(
        "INSERT INTO files (file_id, user_id, file_name, file_size) VALUES (?, ?, ?, ?)",
        preparedStatement -> {
          preparedStatement.setObject(1, fileId);
          preparedStatement.setObject(2, userId);
          preparedStatement.setString(3, fileName);
          preparedStatement.setLong(4, fileSize);
        });
  }
}
