package ru.kicshikxo.filelink.util;

public class ShortId {
  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int BASE = ALPHABET.length();

  public static String encode(long value) {
    if (value == 0) {
      return String.valueOf(ALPHABET.charAt(0));
    }

    StringBuilder shortId = new StringBuilder();
    while (value > 0) {
      int digitIndex = (int) (value % BASE);
      shortId.insert(0, ALPHABET.charAt(digitIndex));
      value /= BASE;
    }
    return shortId.toString();
  }

  public static long decode(String code) {
    long number = 0;
    for (char symbol : code.toCharArray()) {
      int symbolIndex = ALPHABET.indexOf(symbol);
      if (symbolIndex == -1) {
        throw new IllegalArgumentException("INVALID CHAR: " + symbol);
      }
      number = number * BASE + symbolIndex;
    }
    return number;
  }
}
