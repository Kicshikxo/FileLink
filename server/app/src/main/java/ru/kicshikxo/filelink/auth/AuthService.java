package ru.kicshikxo.filelink.auth;

import java.sql.SQLException;

import ru.kicshikxo.filelink.auth.utils.JwtUtils;
import ru.kicshikxo.filelink.auth.utils.PasswordUtils;
import ru.kicshikxo.filelink.database.dto.UserDto;
import ru.kicshikxo.filelink.database.repository.UserRepository;

public class AuthService {

  public static String login(String email, String plainPassword) throws SQLException {
    UserDto user = UserRepository.getByEmail(email);

    if (user == null || !PasswordUtils.verifyPassword(plainPassword, user.getPassword())) {
      throw new RuntimeException("INVALID CREDENTIALS");
    }

    return JwtUtils.generateToken(user.getUserId());
  }
}
