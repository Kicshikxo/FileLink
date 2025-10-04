package ru.kicshikxo.filelink.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.dto.file.FileDownloadDto;

public class FileDownloadsRepository {
  public static void createForFileId(UUID fileId) throws SQLException {
    Database.update(
        "INSERT INTO file_downloads (file_id) VALUES (?)",
        preparedStatement -> preparedStatement.setObject(1, fileId));
  }

  public static List<FileDownloadDto> getByFileId(UUID fileId) throws SQLException {
    return Database.query(
        "SELECT * FROM file_downloads WHERE file_id = ?",
        preparedStatement -> preparedStatement.setObject(1, fileId),
        resultSet -> new FileDownloadDto(
            (UUID) resultSet.getObject("download_id"),
            (UUID) resultSet.getObject("file_id"),
            resultSet.getTimestamp("download_time")));
  }
}
