package ru.kicshikxo.filelink.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.config.JavalinConfig;
import io.javalin.config.SizeUnit;

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

    config.jetty.multipartConfig.maxFileSize(100, SizeUnit.MB);
    config.jetty.multipartConfig.maxTotalRequestSize(1, SizeUnit.GB);
  }
}
