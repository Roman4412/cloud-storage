package com.pustovalov.cloudstorage.dto.response;

import org.springframework.core.io.InputStreamResource;

public record FileDownloadResponse(String fileName, String contentType, InputStreamResource resource) {

}
