package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.S3Object;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface MinioService {

    void saveFile(File file, InputStream inputStream);

    void saveFolder(Folder folder);

    <T extends S3Object> void delete(T object);

    InputStreamResource getFileAsResource(File file);

    <T extends S3Object> void modify(T object, String newKey);

    ByteArrayInputStream getFolderZip(List<S3Object> objects);

}
