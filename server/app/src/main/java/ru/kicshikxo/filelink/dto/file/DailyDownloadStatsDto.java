package ru.kicshikxo.filelink.dto.file;

public class DailyDownloadStatsDto {
  private String date;
  private int downloads;

  public DailyDownloadStatsDto(String date, int downloads) {
    this.date = date;
    this.downloads = downloads;
  }

  public String getDate() {
    return date;
  }

  public int getDownloads() {
    return downloads;
  }
}
