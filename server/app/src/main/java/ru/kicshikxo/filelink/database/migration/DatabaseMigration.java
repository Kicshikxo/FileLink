package ru.kicshikxo.filelink.database.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kicshikxo.filelink.database.Database;

public class DatabaseMigration {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);

  public static void migrate() {
    try {
      Database.update(
          "CREATE TABLE IF NOT EXISTS users (" +
              "user_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "email TEXT UNIQUE NOT NULL," +
              "\"password\" CHAR(60) NOT NULL," +
              "created_at TIMESTAMP DEFAULT NOW()," +
              "updated_at TIMESTAMP DEFAULT NOW()" +
              ")",
          null);

      Database.update(
          "CREATE TABLE IF NOT EXISTS files (" +
              "file_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "user_id uuid NOT NULL REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE," +
              "file_name TEXT NOT NULL," +
              "file_size BIGINT NOT NULL," +
              "created_at TIMESTAMP DEFAULT NOW()," +
              "updated_at TIMESTAMP DEFAULT NOW()," +
              "deleted_at TIMESTAMP DEFAULT NULL," +
              "expired_at TIMESTAMP DEFAULT NULL" +
              ")",
          null);

      Database.update(
          "CREATE INDEX IF NOT EXISTS index_files_user ON files(user_id)",
          null);

      Database.update(
          "CREATE TABLE IF NOT EXISTS file_downloads (" +
              "download_id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY," +
              "file_id uuid NOT NULL REFERENCES files(file_id) ON UPDATE CASCADE ON DELETE CASCADE," +
              "download_time TIMESTAMP DEFAULT NOW()" +
              ")",
          null);

      Database.update(
          "CREATE INDEX IF NOT EXISTS index_file_downloads_file ON file_downloads(file_id)",
          null);

      logger.info("Tables created or already exist");
    } catch (Exception error) {
      logger.error("Failed to create tables", error);
    }
  }
}
