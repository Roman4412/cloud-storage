package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FolderDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.ObjectsFindRequest;
import com.pustovalov.cloudstorage.dto.response.FileResponse;
import com.pustovalov.cloudstorage.dto.response.FolderDownloadResponse;
import com.pustovalov.cloudstorage.dto.response.FolderResponse;
import com.pustovalov.cloudstorage.dto.response.ObjectsFindResponse;
import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.IllegalActionException;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import com.pustovalov.cloudstorage.mapper.S3ObjectMapper;
import com.pustovalov.cloudstorage.repository.S3ObjectPagingRepository;
import com.pustovalov.cloudstorage.repository.S3ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;

@Slf4j
@RequiredArgsConstructor
@Service
public class ObjectServiceImpl implements ObjectService {

    private final S3ObjectPagingRepository pagingRepository;

    private final S3ObjectRepository repository;

    private final UserService userService;

    private final S3ObjectMapper mapper;

    private final MinioServiceImpl minioService;

    @Override
    public ObjectsFindResponse findObjects(ObjectsFindRequest dto) {
        User user = userService.findByUsername(dto.username()).orElseThrow(ObjectNotFoundException::new);

        List<S3Object> all = pagingRepository.findAllByUserAndNameContains(user, dto.name(), dto.pageable());

        List<FileResponse> files = all.stream()
                .filter(o -> o instanceof File)
                .map(o -> mapper.toFileResponse((File) o))
                .toList();

        List<FolderResponse> folders = all.stream()
                .filter(o -> o instanceof Folder)
                .map(o -> mapper.toFolderResponse((Folder) o))
                .toList();

        return new ObjectsFindResponse(folders, files);
    }

    @Override
    public FolderDownloadResponse downloadAsZip(FolderDownloadRequest dto) {
        if (dto.path().isBlank()) {
            throw new IllegalActionException("it is not possible to download this folder");
        }
        S3Object folder = repository.findByUserUsernameAndObjectKeyEndsWith(dto.username(), dto.path() + SLASH)
                .orElseThrow(ObjectNotFoundException::new);
        List<S3Object> targetObjects = repository.findAllByParentStartsWith(folder.getObjectKey());
        return new FolderDownloadResponse(folder.getName(), new InputStreamResource(minioService.getFolderZip(targetObjects)));
    }
}
