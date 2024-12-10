package com.pustovalov.cloudstorage.dto;

import lombok.Builder;

@Builder
public record RenameFolderDto(
        String name,
        String path,
        String newName,
        String username
) {

}
