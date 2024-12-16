package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;
import static com.pustovalov.cloudstorage.utils.PathUtils.getRootNameFor;

@RequiredArgsConstructor
@Service
public class RootFolderInitializer {

    private final S3ObjectRepository repository;

    private final MinioService minioService;

    public void initialize(User user) {
        String name = getRootNameFor(user);
        String objectKey = SLASH + name + SLASH;
        Folder savedFolder = repository.save(new Folder(name, objectKey, null, user));
        minioService.saveFolder(savedFolder);
    }
}
