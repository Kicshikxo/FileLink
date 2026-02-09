package ru.kicshikxo.filelink.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.util.NaiveRateLimit;
import ru.kicshikxo.filelink.config.ServerConfig;
import ru.kicshikxo.filelink.dto.auth.LoginRequestDto;
import ru.kicshikxo.filelink.middleware.AuthMiddleware;
import ru.kicshikxo.filelink.service.AuthService;

public class AuthController {
  private final AuthService authService = new AuthService();

  public void registerRoutes(Javalin app) {
    app.get("/api/auth/check", this::checkAuth);
    app.post("/api/auth/login", this::handleLogin);
    app.post("/api/auth/register", this::handleRegister);
    app.get("/api/auth/logout", this::handleLogout);

    app.exception(JsonProcessingException.class, (error, ctx) -> {
      throw new BadRequestResponse(error.toString());
    });
  }

  private void checkAuth(Context ctx) {
    new AuthMiddleware().handle(ctx);
  }

  private void handleLogin(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);

    try {
      LoginRequestDto loginRequest = ctx.bodyAsClass(LoginRequestDto.class);
      String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

      ctx.cookie(ServerConfig.AUTH_COOKIE_NAME, token, ServerConfig.JWT_EXPIRATION_SECONDS);
      ctx.json(Map.of("token", token));
    } catch (RuntimeException error) {
      throw new UnauthorizedResponse(error.toString());
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void handleRegister(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);

    try {
      LoginRequestDto registerRequest = ctx.bodyAsClass(LoginRequestDto.class);
      String token = authService.register(registerRequest.getEmail(), registerRequest.getPassword());

      ctx.cookie(ServerConfig.AUTH_COOKIE_NAME, token, ServerConfig.JWT_EXPIRATION_SECONDS);
      ctx.json(Map.of("token", token));
    } catch (RuntimeException error) {
      throw new BadRequestResponse(error.toString());
    } catch (Exception error) {
      throw new InternalServerErrorResponse(error.toString());
    }
  }

  private void handleLogout(Context ctx) {
    NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);

    ctx.removeCookie(ServerConfig.AUTH_COOKIE_NAME);
    ctx.json(Map.of("success", true));
  }
}
