package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.DeleteFileDto;
import com.pustovalov.cloudstorage.dto.DownloadFileDto;
import com.pustovalov.cloudstorage.dto.RenameFileDto;
import com.pustovalov.cloudstorage.dto.UploadFileDto;
import com.pustovalov.cloudstorage.dto.request.RenameFileRequest;
import com.pustovalov.cloudstorage.service.FileServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static com.pustovalov.cloudstorage.utils.PathUtils.parsePath;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileService;

    @PostMapping(value = "/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart MultipartFile file, HttpServletRequest request,
                                    @AuthenticationPrincipal String username) throws IOException {
        String filePath = parsePath(request.getRequestURI());
        fileService.save(new UploadFileDto(username, "file", filePath, file.getSize(), null, file.getInputStream()));

        return ResponseEntity.created(URI.create("")).build();
    }

    @GetMapping("/**")
    public ResponseEntity<InputStreamResource> download(@RequestParam String fileName, HttpServletRequest request,
                                                        @AuthenticationPrincipal String owner) {
        String filePath = parsePath(request.getRequestURI());
        InputStreamResource resource = fileService.download(new DownloadFileDto(fileName, filePath, owner));

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(resource);
    }

    @PatchMapping("/**")
    public ResponseEntity<?> rename(@RequestBody RenameFileRequest renameFileRequest, HttpServletRequest request,
                                    @AuthenticationPrincipal String username) {
        String filePath = parsePath(request.getRequestURI());
        fileService.rename(
                new RenameFileDto(filePath, username, renameFileRequest.oldName(), renameFileRequest.newName()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> delete(@RequestParam String fileName, HttpServletRequest request,
                                    @AuthenticationPrincipal String owner) {
        String filePath = parsePath(request.getRequestURI());
        fileService.delete(new DeleteFileDto(fileName, filePath, owner));

        return ResponseEntity.ok().build();
    }

}
