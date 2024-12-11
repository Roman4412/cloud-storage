package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.DeleteFileDto;
import com.pustovalov.cloudstorage.dto.DownloadFileDto;
import com.pustovalov.cloudstorage.dto.RenameFileDto;
import com.pustovalov.cloudstorage.dto.UploadFileDto;
import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.MinioOperationException;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import com.pustovalov.cloudstorage.utils.PathUtils;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    @Value("${minio.default-bucket}")
    private String bucket;

    private final S3ObjectRepository repository;

    private final UserService userService;

    private final MinioClient minioClient;

    public void save(UploadFileDto dto) {
        //TODO нужно проверять, существует ли директория

        // сбилдить экземпляр File
        String fullPath = PathUtils.buildFullPath(dto.owner(), dto.path());
        User owner = userService.findByUsername(dto.owner()).orElseThrow(ObjectNotFoundException::new);
        String objectKey = fullPath + PathUtils.PATH_SEPARATOR + dto.fileName();
        String fileName = dto.fileName();
        Long size = dto.size();

        File file = new File(fileName, fullPath, objectKey, owner, size);
        // сохранить в sql
        repository.save(file);
        //  сохранить в s3
        try {
            minioClient.putObject(PutObjectArgs.builder()
                                          .bucket(bucket)
                                          .object(objectKey)
                                          // .contentType()
                                          .stream(dto.byteStream(), size, -1)
                                          .build());
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    @Transactional
    public void rename(RenameFileDto dto) {
        // подготовить параметры
        String oldName = dto.oldName();
        String newName = dto.newName();
        String owner = dto.owner();
        String fullPath = PathUtils.buildFullPath(owner, dto.path());

//        log.debug("Attempting to rename folder '{}' located at '{}'. Subfolders path: '{}'.", dto.name(), fullPath,
//                  subFoldersPath
//                 );

        S3Object file = repository.findByPathAndName(fullPath, oldName).orElseThrow(ObjectNotFoundException::new);
        // переименовать файл в sql бд, objKey не трогать
        file.setName(newName);
        // вернуть измененный объект?
    }

    @Transactional
    public void delete(DeleteFileDto dto) {
        String fullPath = PathUtils.buildFullPath(dto.owner(), dto.path());
        S3Object file =
                repository.findByPathAndName(fullPath, dto.fileName()).orElseThrow(ObjectNotFoundException::new);

        repository.deleteById(file.getId());

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                                             .bucket(bucket)
                                             .object(file.getObjectKey())
                                             .build());
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    @Override
    public InputStreamResource download(DownloadFileDto dto) {
        String fullPath = PathUtils.buildFullPath(dto.owner(), dto.path());
        S3Object file =
                repository.findByPathAndName(fullPath, dto.fileName()).orElseThrow(ObjectNotFoundException::new);

        GetObjectResponse getObjectresponse;
        try {
            getObjectresponse = minioClient.getObject(GetObjectArgs.builder()
                                                              .bucket(bucket)
                                                              .object(file.getObjectKey())
                                                              .build());
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
        return new InputStreamResource(getObjectresponse);
    }
}
