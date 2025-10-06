package ru.kicshikxo.filelink.dto.file;

public class RenameFileRequestDto {
  public String name;

  RenameFileRequestDto() {
  }

  RenameFileRequestDto(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
