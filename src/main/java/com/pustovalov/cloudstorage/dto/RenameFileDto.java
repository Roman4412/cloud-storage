package com.pustovalov.cloudstorage.dto;

public record RenameFileDto(String path, String owner, String oldName, String newName) {

}
