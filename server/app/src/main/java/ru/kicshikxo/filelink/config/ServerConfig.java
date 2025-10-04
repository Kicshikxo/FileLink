package ru.kicshikxo.filelink.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.config.JavalinConfig;

public class ServerConfig {
  private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

  public static final int PORT = Integer.parseInt(dotenv.get("PORT", "7070"));

  public static void configure(JavalinConfig config) {
    config.http.gzipOnlyCompression();

    config.bundledPlugins.enableCors(cors -> {
      cors.addRule(it -> {
        it.anyHost();
      });
    });
  }
}
