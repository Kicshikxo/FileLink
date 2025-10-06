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

  public static void renameById(UUID fileId, String fileName) throws SQLException {
    Database.update(
        "UPDATE files SET file_name = ?, updated_at = now() WHERE file_id = ?",
        preparedStatement -> {
          preparedStatement.setString(1, fileName);
          preparedStatement.setObject(2, fileId);
        });
  }

  public static void deleteById(UUID fileId) throws SQLException {
    Database.update(
        "UPDATE files SET deleted_at = now() WHERE file_id = ?",
        preparedStatement -> preparedStatement.setObject(1, fileId));
  }

  public static void expireById(UUID fileId) throws SQLException {
    Database.update(
        "UPDATE files SET expired_at = now() WHERE file_id = ?",
        preparedStatement -> preparedStatement.setObject(1, fileId));
  }

  public static List<FileDto> getByUserId(UUID userId) throws SQLException {
    return Database.query(
        "SELECT * FROM files WHERE user_id = ? AND deleted_at IS NULL AND expired_at IS NULL ORDER BY created_at DESC",
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

  public static long getUserFilesSize(UUID userId) throws SQLException {
    return Database.queryFirst(
        "SELECT COALESCE(SUM(file_size), 0) FROM files WHERE user_id = ? AND deleted_at IS NULL AND expired_at IS NULL",
        preparedStatement -> preparedStatement.setObject(1, userId),
        resultSet -> resultSet.getLong(1));
  }

  public static List<FileDto> getExpiredFiles() throws SQLException {
    String sql = "SELECT files.* " +
        "FROM files " +
        "LEFT JOIN ( " +
        "    SELECT file_id, MAX(download_time) AS last_download_time " +
        "    FROM file_downloads " +
        "    GROUP BY file_id " +
        ") AS last_downloads ON files.file_id = last_downloads.file_id " +
        "WHERE files.deleted_at IS NULL " +
        "  AND files.expired_at IS NULL " +
        "  AND COALESCE(last_downloads.last_download_time, files.created_at) < NOW() - INTERVAL '1 day'";

    return Database.query(sql, null, resultSet -> new FileDto(
        (UUID) resultSet.getObject("file_id"),
        (UUID) resultSet.getObject("user_id"),
        resultSet.getString("file_name"),
        resultSet.getLong("file_size"),
        resultSet.getTimestamp("created_at"),
        resultSet.getTimestamp("updated_at"),
        resultSet.getTimestamp("deleted_at") != null ? resultSet.getTimestamp("deleted_at") : null,
        resultSet.getTimestamp("expired_at") != null ? resultSet.getTimestamp("expired_at") : null));
  }

}
