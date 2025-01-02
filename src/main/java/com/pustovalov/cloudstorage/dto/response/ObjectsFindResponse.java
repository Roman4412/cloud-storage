package com.pustovalov.cloudstorage.dto.response;

import java.util.List;

public record ObjectsFindResponse(List<FolderResponse> folders, List<FileResponse> files) {

}
