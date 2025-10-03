package ru.kicshikxo.filelink.auth.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

  public static String hashPassword(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt(8));
  }

  public static boolean verifyPassword(String plainPassword, String hashedPassword) {
    if (hashedPassword == null || hashedPassword.isEmpty()) {
      return false;
    }

    return BCrypt.checkpw(plainPassword, hashedPassword);
  }
}
