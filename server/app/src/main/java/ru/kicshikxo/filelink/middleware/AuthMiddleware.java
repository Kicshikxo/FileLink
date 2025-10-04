package ru.kicshikxo.filelink.middleware;

import java.util.UUID;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import ru.kicshikxo.filelink.service.AuthService;

public class AuthMiddleware {
  private final AuthService authService = new AuthService();

  public void handle(Context ctx) {
    String headerToken = ctx.header("Authorization");
    String cookieToken = ctx.cookie("filelink-token");

    String token = headerToken != null ? headerToken.substring("Bearer ".length()) : cookieToken;

    if (token == null) {
      throw new UnauthorizedResponse("NOT AUTHORIZED");
    }

    try {
      UUID userId = authService.verifyToken(token);
      ctx.attribute("userId", userId);
    } catch (Exception error) {
      throw new UnauthorizedResponse("INVALID TOKEN");
    }
  }
}
