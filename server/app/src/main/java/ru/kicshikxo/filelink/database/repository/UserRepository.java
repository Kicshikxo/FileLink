package ru.kicshikxo.filelink.database.repository;

import java.sql.SQLException;
import java.util.UUID;

import ru.kicshikxo.filelink.database.Database;
import ru.kicshikxo.filelink.dto.file.UserDto;

public class UserRepository {
  public static UserDto getById(UUID userId) throws SQLException {
    return Database.queryFirst(
        "SELECT * FROM users WHERE user_id = ?",
        preparedStatement -> preparedStatement.setObject(1, userId),
        resultSet -> new UserDto(
            (UUID) resultSet.getObject("user_id"),
            resultSet.getString("email"),
            resultSet.getString("password"),
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
            resultSet.getString("password"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("updated_at")));
  }
}
