package com.pustovalov.cloudstorage.dto;

import java.io.InputStream;

public record UploadFileDto(
        String owner,
        String fileName,

        String path,
        Long size,
        String contentType,
        InputStream byteStream

) {

}
