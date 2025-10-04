package ru.kicshikxo.filelink.dto.file;

import java.util.Date;

public class DailyDownloadStatsDto {
  private Date date;
  private int downloads;

  public DailyDownloadStatsDto(Date date, int downloads) {
    this.date = date;
    this.downloads = downloads;
  }

  public Date getDate() {
    return date;
  }

  public int getDownloads() {
    return downloads;
  }
}
