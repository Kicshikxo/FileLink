package ru.kicshikxo.filelink.dto.auth;

public class RegisterRequestDto {
  private String email;
  private String password;

  public RegisterRequestDto() {
  }

  public RegisterRequestDto(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
