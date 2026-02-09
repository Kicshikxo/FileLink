package ru.kicshikxo.filelink.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.config.JavalinConfig;
import io.javalin.config.SizeUnit;

public class ServerConfig {
  private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

  public static final int PORT = Integer.parseInt(dotenv.get("PORT", "7070"));

  public static final String DATABASE_URL = dotenv.get("DATABASE_URL");
  public static final String DATABASE_USER = dotenv.get("DATABASE_USER");
  public static final String DATABASE_PASSWORD = dotenv.get("DATABASE_PASSWORD");

  public static final String AUTH_COOKIE_NAME = dotenv.get("AUTH_COOKIE_NAME", "filelink-token");
  public static final String JWT_SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
  // 2592000 = 30 * 24 * 60 * 60 = 30 days
  public static final int JWT_EXPIRATION_SECONDS = Integer.parseInt(dotenv.get("JWT_EXPIRATION_SECONDS", "2592000"));

  // 104857600 = 100 * 1024 * 1024 = 100 MB
  public static final long MAX_FILE_SIZE_BYTES = Long.parseLong(dotenv.get("MAX_FILE_SIZE_BYTES", "104857600"));
  // 1073741824 = 1 * 1024 * 1024 * 1024 = 1 GB
  public static final long MAX_USER_FILES_SIZE_BYTES = Long
      .parseLong(dotenv.get("MAX_USER_FILES_SIZE_BYTES", "1073741824"));
  // 86400 = 24 * 60 * 60 = 1 day
  public static final long FILE_TTL_SECONDS = Long.parseLong(dotenv.get("FILE_TTL_SECONDS", "86400"));
  public static final String UPLOADS_DIRECTORY = dotenv.get("UPLOADS_DIRECTORY", "uploads");

  public static void configure(JavalinConfig config) {
    config.http.gzipOnlyCompression();

    config.bundledPlugins.enableCors(cors -> {
      cors.addRule(it -> {
        it.anyHost();
      });
    });

    config.jetty.multipartConfig.maxFileSize(MAX_FILE_SIZE_BYTES, SizeUnit.BYTES);
    config.jetty.multipartConfig.maxTotalRequestSize(MAX_USER_FILES_SIZE_BYTES, SizeUnit.BYTES);
  }
}
