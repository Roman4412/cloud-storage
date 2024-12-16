package com.pustovalov.cloudstorage.mapper;

import com.pustovalov.cloudstorage.dto.response.FileResponse;
import com.pustovalov.cloudstorage.dto.response.FolderGetContentResponse;
import com.pustovalov.cloudstorage.dto.response.FolderResponse;
import com.pustovalov.cloudstorage.entity.File;
import com.pustovalov.cloudstorage.entity.Folder;
import com.pustovalov.cloudstorage.utils.PathUtils;
import org.mapstruct.*;

import java.util.List;

import static com.pustovalov.cloudstorage.utils.PathUtils.SLASH;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface S3ObjectMapper {

    FolderGetContentResponse toGetContentResponse(List<FolderResponse> folders, List<FileResponse> files);

    @Mapping(source = "objectKey", target = "path", qualifiedByName = "formatPath")
    FileResponse toFileResponse(File file);

    @Mapping(source = "objectKey", target = "path", qualifiedByName = "formatPath")
    FolderResponse toFolderResponse(Folder folder);

    @Named("formatPath")
    static String formatPath(String objectKey) {
//
//        String[] parts = objectKey.split(SLASH);
//        parts[0] = "";
//        parts[1] = "";
//
//        StringBuilder result = new StringBuilder();
//        for (String p : parts) {
//            if (!p.isEmpty()) {
//                result.append(SLASH).append(p);
//            }
//        }
//
//        return result.toString();

        return PathUtils.removeRoot(objectKey);
    }
}