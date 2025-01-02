package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FileDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FileDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.FileRenameRequest;
import com.pustovalov.cloudstorage.dto.request.FileUploadRequest;
import com.pustovalov.cloudstorage.dto.response.FileDownloadResponse;
import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;
import static com.pustovalov.cloudstorage.utils.PathUtils.getRootNameFor;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final S3ObjectRepository repository;

    private final MinioServiceImpl minioService;

    private final UserService userService;

    @Transactional
    public void save(FileUploadRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String root = getRootNameFor(user);
        String parentKey = SLASH + root + dto.location() + SLASH;

        Optional<S3Object> optionalParentFolder = repository.findByObjectKey(parentKey);
        if (optionalParentFolder.isEmpty()) {
            throw new ObjectNotFoundException("The directory selected for the download does not exist");
        }

        S3Object parentFolder = optionalParentFolder.get();
        String fileName = dto.fileName();
        String objectKey = parentFolder.getObjectKey() + fileName;
        Long size = dto.size();
        File savedFile = repository.save(File.builder()
                                        .name(fileName)
                                        .objectKey(objectKey)
                                        .parent(parentFolder.getObjectKey())
                                        .user(parentFolder.getUser())
                                        .size(size)
                                        .contentType(dto.contentType())
                                        .build());

        minioService.saveFile(savedFile, dto.inputStream());
    }

    @Transactional
    public void rename(FileRenameRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String root = getRootNameFor(user);
        String objectKey = SLASH + root + dto.path();

        S3Object file = repository.findByObjectKey(objectKey).orElseThrow(() -> new ObjectNotFoundException("File not found"));
        String newObjectKey = file.getParent() + dto.newName();
        minioService.modify(file, newObjectKey);
        file.setName(dto.newName());
        file.setObjectKey(newObjectKey);
    }

    @Transactional
    public void delete(FileDeleteRequest dto) {
        File file = (File) repository.findByUserUsernameAndObjectKeyEndsWith(dto.username(), dto.path())
                .orElseThrow(ObjectNotFoundException::new);
        repository.delete(file);
        minioService.delete(file);
    }

    @Transactional
    @Override
    public FileDownloadResponse download(FileDownloadRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String root = getRootNameFor(user);
        String objectKey = SLASH + root + dto.path();

        File file = repository.findByObjectKey(objectKey).map(f -> (File) f)
                .orElseThrow(() -> new ObjectNotFoundException("File not found"));

        InputStreamResource resource = new InputStreamResource(minioService.getFileAsResource(file));
        return new FileDownloadResponse(file.getName(), file.getContentType(), resource);
    }

}