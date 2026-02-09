package ru.kicshikxo.filelink.service;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ru.kicshikxo.filelink.config.ServerConfig;
import ru.kicshikxo.filelink.database.repository.UserRepository;
import ru.kicshikxo.filelink.dto.file.UserDto;

public class AuthService {
  private static final SecretKey SECRET_KEY = Keys
      .hmacShaKeyFor(ServerConfig.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  public String login(String email, String password) throws SQLException, RuntimeException {
    UserDto user = UserRepository.getByEmail(email);
    if (user == null) {
      throw new RuntimeException("INVALID CREDENTIALS");
    }

    if (!BCrypt.checkpw(password, user.getPasswordHash())) {
      throw new RuntimeException("INVALID CREDENTIALS");
    }

    return generateToken(user.getUserId());
  }

  public String register(String email, String password) throws SQLException, RuntimeException {
    UserDto existingUser = UserRepository.getByEmail(email);
    if (existingUser != null) {
      throw new RuntimeException("USER ALREADY EXISTS");
    }

    UserDto user = UserRepository.create(email, BCrypt.hashpw(password, BCrypt.gensalt()));

    return generateToken(user.getUserId());
  }

  public String generateToken(UUID userId) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(new Date(now))
        .expiration(new Date(now + ServerConfig.JWT_EXPIRATION_SECONDS * 1000L))
        .signWith(SECRET_KEY)
        .compact();
  }

  public UUID verifyToken(String token) {
    try {

      Claims claims = Jwts.parser()
          .verifyWith(SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return UUID.fromString(claims.getSubject());
    } catch (Exception error) {
      return null;
    }
  }
}
