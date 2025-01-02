package com.pustovalov.cloudstorage.dto.response;

import java.util.List;

public record FolderGetContentResponse(List<FolderResponse> folders, List<FileResponse> files) {

}
