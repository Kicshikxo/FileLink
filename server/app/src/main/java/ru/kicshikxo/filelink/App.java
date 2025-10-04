package ru.kicshikxo.filelink;

import io.javalin.Javalin;
import ru.kicshikxo.filelink.config.ServerConfig;
import ru.kicshikxo.filelink.controller.AuthController;
import ru.kicshikxo.filelink.controller.FileController;
import ru.kicshikxo.filelink.database.migration.DatabaseMigration;

public class App {
  public static void main(String[] args) {
    DatabaseMigration.migrate();

    Javalin app = Javalin.create(ServerConfig::configure).start(ServerConfig.PORT);

    new AuthController().registerRoutes(app);
    new FileController().registerRoutes(app);
  }
}
