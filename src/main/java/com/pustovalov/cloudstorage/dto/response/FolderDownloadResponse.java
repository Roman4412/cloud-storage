package com.pustovalov.cloudstorage.dto.response;

import org.springframework.core.io.InputStreamResource;

public record FolderDownloadResponse(String folderName, InputStreamResource resource) {

}
