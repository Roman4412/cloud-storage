package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FolderDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.ObjectsFindRequest;
import com.pustovalov.cloudstorage.dto.response.FolderDownloadResponse;
import com.pustovalov.cloudstorage.dto.response.ObjectsFindResponse;

public interface ObjectService {
    ObjectsFindResponse findObjects(ObjectsFindRequest dto);

    FolderDownloadResponse downloadAsZip(FolderDownloadRequest dto);
}
