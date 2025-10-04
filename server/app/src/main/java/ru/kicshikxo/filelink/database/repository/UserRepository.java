package ru.kicshikxo.filelink.database.repository;

import java.sql.SQLException;
import java.util.UUID;

import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.dto.file.UserDto;

public class UserRepository {
  public static UserDto create(String email, String password_hash) throws SQLException {
    return Database.queryFirst(
        "INSERT INTO users (email, password_hash) VALUES (?, ?) RETURNING *",
        preparedStatement -> {
          preparedStatement.setString(1, email);
          preparedStatement.setString(2, password_hash);
        },
        resultSet -> new UserDto(
            (UUID) resultSet.getObject("user_id"),
            resultSet.getString("email"),
            resultSet.getString("password_hash"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("updated_at")));
  }

  public static UserDto getByEmail(String email) throws SQLException {
    return Database.queryFirst(
        "SELECT * FROM users WHERE email = ?",
        preparedStatement -> preparedStatement.setString(1, email),
        resultSet -> new UserDto(
            (UUID) resultSet.getObject("user_id"),
            resultSet.getString("email"),
            resultSet.getString("password_hash"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("updated_at")));
  }
}
