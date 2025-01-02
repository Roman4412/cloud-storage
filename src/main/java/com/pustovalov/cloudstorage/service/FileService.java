package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FileDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FileDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.FileRenameRequest;
import com.pustovalov.cloudstorage.dto.request.FileUploadRequest;
import com.pustovalov.cloudstorage.dto.response.FileDownloadResponse;

public interface FileService {

    void save(FileUploadRequest dto);

    void rename(FileRenameRequest dto);

    void delete(FileDeleteRequest dto);

    FileDownloadResponse download(FileDownloadRequest dto);
}
