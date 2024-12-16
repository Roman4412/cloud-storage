package com.pustovalov.cloudstorage.dto.request;

import lombok.Builder;

@Builder
public record FolderRenameRequest(
        String path,
        String newName,
        String username
) {

}
