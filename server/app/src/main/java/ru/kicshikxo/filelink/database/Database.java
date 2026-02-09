package ru.kicshikxo.filelink.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.kicshikxo.filelink.config.ServerConfig;

public class Database {

  @FunctionalInterface
  public interface StatementSetter<T> {
    void set(T t) throws SQLException;
  }

  @FunctionalInterface
  public interface ResultMapper<T, R> {
    R apply(T t) throws SQLException;
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        ServerConfig.DATABASE_URL, ServerConfig.DATABASE_USER, ServerConfig.DATABASE_PASSWORD);
  }

  public static int update(String sqlQuery, StatementSetter<PreparedStatement> statementSetter)
      throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

      if (statementSetter != null) {
        statementSetter.set(statement);
      }

      return statement.executeUpdate();
    }
  }

  public static <T> List<T> query(String sqlQuery,
      StatementSetter<PreparedStatement> statementSetter,
      ResultMapper<ResultSet, T> resultMapper) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

      if (statementSetter != null) {
        statementSetter.set(statement);
      }

      try (ResultSet resultSet = statement.executeQuery()) {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) {
          results.add(resultMapper.apply(resultSet));
        }
        return results;
      }
    }
  }

  public static <T> T queryFirst(String sql, StatementSetter<PreparedStatement> statementSetter,
      ResultMapper<ResultSet, T> resultMapper) throws SQLException {
    List<T> list = query(sql, statementSetter, resultMapper);
    return list.isEmpty() ? null : list.get(0);
  }
}
