package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.CreateFolderDto;
import com.pustovalov.cloudstorage.dto.DeleteFolderDto;
import com.pustovalov.cloudstorage.dto.GetContentDto;
import com.pustovalov.cloudstorage.dto.RenameFolderDto;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.pustovalov.cloudstorage.utils.PathUtils.buildFullPath;
import static com.pustovalov.cloudstorage.utils.PathUtils.PATH_SEPARATOR;
import static com.pustovalov.cloudstorage.utils.PathUtils.buildSubFoldersPath;

@Slf4j
@RequiredArgsConstructor
@Service
public class FolderService {

    private final UserService userService;

    private final S3ObjectRepository repository;

    @Transactional
    public void saveFolderMetaData(CreateFolderDto dto) {
        //TODO обработать ограничение уникальности
        String username = dto.username();
        String folderName = dto.folderName();
        String fullPath = buildFullPath(username, dto.path());
        String objectKey = fullPath + PATH_SEPARATOR + folderName;
        User creator = userService.findByUsername(username).orElseThrow(ObjectNotFoundException::new);

        Folder savedFolder = repository.save(new Folder(folderName, fullPath, objectKey, creator));
        log.debug("Folder creation succeeded: id={}, name={}, path={}, objectKey={}, userId={}", savedFolder.getId(),
                  savedFolder.getName(), savedFolder.getPath(), savedFolder.getObjectKey(), savedFolder.getUser().getId());
    }

    public void createPersonalFolder(User user) {
        S3Object folder = new Folder(user.getUsername(), PATH_SEPARATOR, PATH_SEPARATOR + user.getUsername(), user);

        repository.save(folder);
        log.debug("Personal folder creation succeeded: id={}, name={}, path={}, objectKey={}, userId={}",
                  folder.getId(), folder.getName(), folder.getPath(), folder.getObjectKey(), folder.getUser().getId());
    }

    public List<String> getContent(GetContentDto dto) {
        String fullPath = buildFullPath(dto.username(), dto.path());

        return repository.findAllByPath(fullPath).stream()
                .map(S3Object::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(DeleteFolderDto dto) {
        String fullPath = buildFullPath(dto.username(), dto.path());
        String subFoldersPath = buildSubFoldersPath(dto.folderName(), fullPath);

        log.debug("Attempting to delete folder '{}' located at '{}'. Subfolders path: '{}'.", dto.folderName(),
                  fullPath, subFoldersPath);

        repository.deleteByPathAndName(fullPath, dto.folderName());
        repository.deleteAllByPathStartingWith(subFoldersPath);
    }

    @Transactional
    public void rename(RenameFolderDto dto) {
        String folderName = dto.name();
        String newFolderName = dto.newName();
        String fullPath = buildFullPath(dto.username(), dto.path());
        String subFoldersPath = buildSubFoldersPath(folderName, fullPath);

        S3Object target = repository.findByPathAndName(fullPath, folderName).orElseThrow(ObjectNotFoundException::new);
        List<S3Object> nestedObjects = repository.findAllByPathStartsWith(subFoldersPath);

        log.debug("Attempting to rename folder '{}' located at '{}'. Subfolders path: '{}'.", dto.name(), fullPath,
                  subFoldersPath);

        target.setName(newFolderName);
        if (!nestedObjects.isEmpty()) {
            for (S3Object o : nestedObjects) {
                o.setPath(o.getPath().replace(folderName, newFolderName));
            }
        }
    }
    //    private static List<Folder> generateSubFolders(String path, User creator) {
    //        //TODO можно порефакторить
    //        String[] split = path.split(PATH_SEPARATOR);
    //        List<Folder> folders = new ArrayList<>();
    //
    //        StringBuilder tmpKey = new StringBuilder();
    //
    //        for(String p : split) {
    //            if (!p.isEmpty()) {
    //                tmpKey.append(PATH_SEPARATOR).append(p);
    //                folders.add(createFolder(tmpKey.toString(), creator));
    //            }
    //        }
    //        return folders;
    //    }
    //
    //    private static Folder createFolder(String objectKey, User creator) {
    //        String[] keyParts = objectKey.split(PATH_SEPARATOR);
    //        String folderName = keyParts[keyParts.length - 1];
    //        String extractedPath = objectKey.replace(PATH_SEPARATOR + folderName, "");
    //
    //        if ((extractedPath.isEmpty())) {
    //            extractedPath = PATH_SEPARATOR;
    //        }
    //        return new Folder(folderName, extractedPath, creator);
    //    }

}
