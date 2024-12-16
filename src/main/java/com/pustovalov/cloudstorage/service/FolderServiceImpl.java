package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FolderCreateRequest;
import com.pustovalov.cloudstorage.dto.request.FolderDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FolderGetContentRequest;
import com.pustovalov.cloudstorage.dto.request.FolderRenameRequest;
import com.pustovalov.cloudstorage.dto.response.FileResponse;
import com.pustovalov.cloudstorage.dto.response.FolderGetContentResponse;
import com.pustovalov.cloudstorage.dto.response.FolderResponse;
import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.IllegalActionException;
import com.pustovalov.cloudstorage.exception.ObjectCreationException;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import com.pustovalov.cloudstorage.mapper.S3ObjectMapper;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;
import static com.pustovalov.cloudstorage.utils.PathUtils.getRootNameFor;

@Slf4j
@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final S3ObjectRepository repository;

    private final UserService userService;

    private final S3ObjectMapper s3ObjectMapper;

    private final MinioServiceImpl minioService;

    @Transactional
    public void createFolder(FolderCreateRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String root = getRootNameFor(user);
        String parentKey = SLASH + root + dto.location() + SLASH;

        if (repository.findByObjectKey(parentKey).isEmpty()) {
            throw new ObjectCreationException("it is not possible to create a folder because the parent directory does not exist");
        }

        String name = dto.name();
        String objectKey = parentKey + name + SLASH;
        Folder newFolder = new Folder(name, objectKey, parentKey, user);
        repository.save(newFolder);
        minioService.saveFolder(newFolder);
    }

    @Transactional
    public FolderGetContentResponse getContent(FolderGetContentRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String rootName = getRootNameFor(user);
        String objectKey = SLASH + rootName + dto.path() + SLASH;

        if (repository.findByObjectKey(objectKey).isEmpty()) {
            throw new ObjectNotFoundException("The requested folder does not exist");
        }
        List<S3Object> targetObjects = repository.findAllByParent(objectKey);

        List<FileResponse> files = targetObjects.stream()
                .filter(o -> o instanceof File)
                .map(o -> s3ObjectMapper.toFileResponse((File) o))
                .toList();

        List<FolderResponse> folders = targetObjects.stream()
                .filter(o -> o instanceof Folder)
                .map(o -> s3ObjectMapper.toFolderResponse((Folder) o))
                .toList();

        return s3ObjectMapper.toGetContentResponse(folders, files);
    }

    @Transactional
    public void delete(FolderDeleteRequest dto) {
        if (dto.path().isEmpty()) {
            throw new IllegalActionException("It is not possible to delete this folder");
        }
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String rootName = getRootNameFor(user);
        String objectKey = SLASH + rootName + dto.path() + SLASH;
        S3Object folder =
                repository.findByObjectKey(objectKey)
                        .orElseThrow(() -> new ObjectNotFoundException("The requested folder does not exist"));
        repository.delete(folder);
        repository.deleteAllByParentStartsWith(folder.getObjectKey());
        minioService.delete(folder);
    }

    @Transactional
    public void rename(FolderRenameRequest dto) {
        if (dto.path().isBlank()) {
            throw new IllegalActionException("it is not possible to rename this folder");
        }
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);
        String rootName = getRootNameFor(user);
        String objectKey = SLASH + rootName + dto.path() + SLASH;
        S3Object folder =
                repository.findByObjectKey(objectKey)
                        .orElseThrow(() -> new ObjectNotFoundException("The requested folder does not exist"));
        String newName = dto.newName();
        String newObjectKey = folder.getParent() + newName + SLASH;
        List<S3Object> nestedObjects = repository.findAllByParentStartsWith(objectKey);

        folder.setObjectKey(newObjectKey);
        folder.setName(newName);
        for (S3Object o : nestedObjects) {
            String immutableKeyPart = o.getObjectKey().substring(objectKey.length());
            String newKey = newObjectKey + immutableKeyPart;
            minioService.modify(o, newKey);
            o.setObjectKey(newKey);
            String parentKeyPart = o.getParent().substring(objectKey.length());
            o.setParent(newObjectKey + parentKeyPart);
        }
    }

}
