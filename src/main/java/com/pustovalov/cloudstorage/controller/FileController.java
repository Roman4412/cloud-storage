package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.FileDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FileDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.FileRenameRequest;
import com.pustovalov.cloudstorage.dto.request.FileUploadRequest;
import com.pustovalov.cloudstorage.dto.response.FileDownloadResponse;
import com.pustovalov.cloudstorage.service.FileServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileService;

    @PostMapping(value = "/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestPart MultipartFile file,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         HttpServletRequest request) throws IOException {
        FileUploadRequest dto = FileUploadRequest.builder()
                .location(parsePath(request.getRequestURI()))
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .inputStream(file.getInputStream())
                .username(userDetails.getUsername())
                .build();
        fileService.save(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> download(@AuthenticationPrincipal UserDetails userDetails,
                                             HttpServletRequest request) {
        FileDownloadRequest dto = new FileDownloadRequest(parsePath(request.getRequestURI()), userDetails.getUsername());
        FileDownloadResponse response = fileService.download(dto);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(response.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.fileName() + "\"")
                .body(response.resource());
    }

    @PatchMapping("/**")
    public ResponseEntity<String> rename(@RequestParam
                                         @Size(min = 1, max = 255, message = "{file.create.name.size}")
                                         @Pattern(regexp = "^[a-zA-Z0-9_.\\-()\\[\\]]+$", message = "{file.create.name.pattern}")
                                         String newName,
                                         @AuthenticationPrincipal
                                         UserDetails userDetails,
                                         HttpServletRequest request) {
        FileRenameRequest dto = new FileRenameRequest(parsePath(request.getRequestURI()), newName, userDetails.getUsername());
        fileService.rename(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/**")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        FileDeleteRequest dto = new FileDeleteRequest(parsePath(request.getRequestURI()), userDetails.getUsername());
        fileService.delete(dto);
        return ResponseEntity.noContent().build();
    }

    private static String parsePath(String requestURI) {
        return requestURI.substring("/files".length());
    }

}