package ru.kicshikxo.filelink.middleware;

import java.util.UUID;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import ru.kicshikxo.filelink.database.repository.UserRepository;
import ru.kicshikxo.filelink.service.AuthService;

public class AuthMiddleware {
  private final AuthService authService = new AuthService();

  private final boolean throwException;

  public AuthMiddleware() {
    this(true);
  }

  public AuthMiddleware(boolean throwException) {
    this.throwException = throwException;
  }

  public void handle(Context ctx) {
    String headerToken = ctx.header("Authorization");
    String cookieToken = ctx.cookie(AuthService.AUTH_COOKIE_NAME);

    String token = headerToken != null ? headerToken.substring("Bearer ".length()) : cookieToken;

    if (token == null && throwException) {
      throw new UnauthorizedResponse("NOT AUTHORIZED");
    }

    try {
      UUID userId = authService.verifyToken(token);

      if (UserRepository.getById(userId) == null && throwException) {
        throw new UnauthorizedResponse("USER NOT FOUND");
      }

      ctx.attribute("userId", userId);
    } catch (UnauthorizedResponse error) {
      throw error;
    } catch (Exception error) {
      throw new UnauthorizedResponse("INVALID TOKEN");
    }
  }
}
