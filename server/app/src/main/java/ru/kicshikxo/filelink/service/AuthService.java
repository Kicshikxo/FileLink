package ru.kicshikxo.filelink.service;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.mindrot.jbcrypt.BCrypt;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ru.kicshikxo.filelink.database.repository.UserRepository;
import ru.kicshikxo.filelink.dto.file.UserDto;

public class AuthService {
  private static final Dotenv dotenv = Dotenv.load();

  private static final String JWT_SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
  private static final SecretKey KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  public static final int EXPIRATION_SECONDS = 30 * 24 * 60 * 60;

  public String login(String email, String password) throws SQLException, RuntimeException {
    UserDto user = UserRepository.getByEmail(email);
    if (user == null) {
      throw new RuntimeException("INVALID CREDENTIALS");
    }

    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new RuntimeException("INVALID CREDENTIALS");
    }

    return generateToken(user.getUserId());
  }

  public String generateToken(UUID userId) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(new Date(now))
        .expiration(new Date(now + EXPIRATION_SECONDS * 1000L))
        .signWith(KEY)
        .compact();
  }

  public UUID verifyToken(String token) {
    try {

      Claims claims = Jwts.parser()
          .verifyWith(KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return UUID.fromString(claims.getSubject());
    } catch (Exception error) {
      return null;
    }
  }
}
