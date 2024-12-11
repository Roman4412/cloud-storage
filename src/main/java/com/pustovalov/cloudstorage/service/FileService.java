package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.DeleteFileDto;
import com.pustovalov.cloudstorage.dto.DownloadFileDto;
import com.pustovalov.cloudstorage.dto.RenameFileDto;
import com.pustovalov.cloudstorage.dto.UploadFileDto;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

public interface FileService {

    void save(UploadFileDto dto);

    void rename(RenameFileDto dto);

    void delete(DeleteFileDto dto);

    InputStreamResource download(DownloadFileDto dto);
}
