package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.exception.MinioOperationException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;
import static com.pustovalov.cloudstorage.utils.PathUtils.removeRoot;

@RequiredArgsConstructor
@Service
public class MinioServiceImpl implements MinioService {

    @Value("${minio.default-bucket}")
    private String bucket;

    private final MinioClient minioClient;

    public void saveFile(File file, InputStream inputStream) {
        PutObjectArgs args = PutObjectArgs.builder().contentType(file.getContentType())
                .bucket(bucket)
                .object(file.getObjectKey())
                .stream(inputStream, file.getSize(), -1).build();
        try {
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    public void saveFolder(Folder folder) {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket)
                .object(folder.getObjectKey())
                .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                .build();
        try {
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    public <T extends S3Object> void delete(T object) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(object.getObjectKey())
                .build();
        try {
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    public InputStreamResource getFileAsResource(File file) {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(file.getObjectKey())
                .build();
        try {
            return new InputStreamResource(minioClient.getObject(args));
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    public <T extends S3Object> void modify(T object, String newKey) {
        try {
            CopySource source = CopySource.builder()
                    .bucket(bucket)
                    .object(object.getObjectKey())
                    .build();

            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(newKey)
                    .source(source)
                    .build();

            minioClient.copyObject(copyArgs);
            delete(object);
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
    }

    public ByteArrayInputStream getFolderZip(List<S3Object> objects) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            for (S3Object o : objects) {
                String objectKey = o.getObjectKey();
                GetObjectArgs args = GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build();

                try (InputStream inputStream = minioClient.getObject(args)) {
                    String entryName = removeRoot(objectKey).substring(SLASH.length());
                    zipOut.putNextEntry(new ZipEntry(entryName));
                    inputStream.transferTo(zipOut);
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new MinioOperationException(e);
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

}
