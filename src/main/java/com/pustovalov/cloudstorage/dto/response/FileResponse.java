package com.pustovalov.cloudstorage.dto.response;

public record FileResponse(
        String name,
        String path,
        Long size,
        String contentType) {

}
