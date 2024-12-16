package com.pustovalov.cloudstorage.dto.request;

public record FileRenameRequest(String path, String newName, String username) {

}
