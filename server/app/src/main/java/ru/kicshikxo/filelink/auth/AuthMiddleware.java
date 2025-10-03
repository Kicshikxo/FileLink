package ru.kicshikxo.filelink.auth;

import java.util.List;
import java.util.UUID;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import ru.kicshikxo.filelink.auth.utils.JwtUtils;

public class AuthMiddleware {
  public static void handle(Context ctx) throws Exception {
    handle(ctx, List.of());
  }

  public static void handle(Context ctx, List<String> excludedPaths) throws Exception {
    for (String exclude : excludedPaths) {
      if (ctx.path().startsWith(exclude)) {
        return;
      }
    }

    String headerToken = ctx.header("Authorization");
    String cookieToken = ctx.cookie("filelink-token");

    String token = headerToken != null ? headerToken.substring("Bearer ".length()) : cookieToken;

    if (token == null) {
      throw new UnauthorizedResponse("NOT AUTHORIZED");
    }

    try {
      UUID userId = JwtUtils.validateTokenAndGetUserId(token);
      ctx.attribute("userId", userId);
    } catch (Exception error) {
      throw new UnauthorizedResponse("INVALID TOKEN");
    }
  }
}
