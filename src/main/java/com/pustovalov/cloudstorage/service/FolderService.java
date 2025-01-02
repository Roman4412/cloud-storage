package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.FolderCreateRequest;
import com.pustovalov.cloudstorage.dto.request.FolderDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FolderGetContentRequest;
import com.pustovalov.cloudstorage.dto.request.FolderRenameRequest;
import com.pustovalov.cloudstorage.dto.response.FolderGetContentResponse;

public interface FolderService {

    void createFolder(FolderCreateRequest dto);

    FolderGetContentResponse getContent(FolderGetContentRequest dto);

    void delete(FolderDeleteRequest dto);

    void rename(FolderRenameRequest dto);

}
