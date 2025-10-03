package ru.kicshikxo.filelink.auth.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {
  public static final int EXPIRATION_SECONDS = 30 * 24 * 60 * 60;

  private static final Dotenv dotenv = Dotenv.load();

  private static final String JWT_SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
  private static final SecretKey KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  public static String generateToken(UUID userId) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(new Date(now))
        .expiration(new Date(now + EXPIRATION_SECONDS * 1000L))
        .signWith(KEY)
        .compact();
  }

  public static UUID validateTokenAndGetUserId(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    return UUID.fromString(claims.getSubject());
  }
}
