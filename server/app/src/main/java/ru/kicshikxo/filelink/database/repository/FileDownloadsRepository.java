package ru.kicshikxo.filelink.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.dto.file.DailyDownloadStatsDto;
import ru.kicshikxo.filelink.dto.file.FileDownloadDto;

public class FileDownloadsRepository {
  public static void createForFileById(UUID fileId) throws SQLException {
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

  public static List<DailyDownloadStatsDto> getFileDownloadStatisticsById(UUID fileId, int days) throws SQLException {
    if (days < 1) {
      throw new IllegalArgumentException("DAYS MUST BE AT LEAST 1");
    }

    String sql = "SELECT day::date AS download_date, COUNT(file_downloads.download_id) AS downloads_count " +
        "FROM generate_series(CURRENT_DATE - (? - 1) * INTERVAL '1 day', CURRENT_DATE, INTERVAL '1 day') AS day " +
        "LEFT JOIN file_downloads " +
        "ON file_downloads.download_time::date = day::date " +
        "AND file_downloads.file_id = ? " +
        "GROUP BY day " +
        "ORDER BY day";

    return Database.query(sql, preparedStatement -> {
      preparedStatement.setInt(1, days);
      preparedStatement.setObject(2, fileId);
    }, resultSet -> new DailyDownloadStatsDto(
        resultSet.getDate("download_date").toString(),
        resultSet.getInt("downloads_count")));
  }
}
