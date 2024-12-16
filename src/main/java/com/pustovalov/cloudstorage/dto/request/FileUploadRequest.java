package com.pustovalov.cloudstorage.dto.request;


import lombok.Builder;

import java.io.InputStream;

@Builder
public record FileUploadRequest(
        String location,
        String fileName,
        Long size,
        String contentType,
        InputStream inputStream,
        String username

) {

}
