package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.FolderDownloadRequest;
import com.pustovalov.cloudstorage.dto.request.ObjectsFindRequest;
import com.pustovalov.cloudstorage.dto.response.FolderDownloadResponse;
import com.pustovalov.cloudstorage.dto.response.ObjectsFindResponse;
import com.pustovalov.cloudstorage.service.ObjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/objects")
@Validated
public class ObjectController {
    //TODO выдает рут директорию при поиске
    //TODO сделать нормальную дто
    private final ObjectService objectService;

    @GetMapping
    public ResponseEntity<?> findObjects(@RequestParam String name,
                                         @RequestParam(defaultValue = "0", required = false) int page,
                                         @RequestParam(defaultValue = "10", required = false) int size,
                                         @RequestParam(defaultValue = "name", required = false) String[] sort,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
        ObjectsFindRequest objectsFindRequestDto = new ObjectsFindRequest(name, userDetails.getUsername(), pageRequest);
        ObjectsFindResponse response = objectService.findObjects(objectsFindRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadObjects(@AuthenticationPrincipal UserDetails userDetails,
                                                    HttpServletRequest request) {
        FolderDownloadResponse response =
                objectService.downloadAsZip(new FolderDownloadRequest(parsePath(request.getRequestURI()), userDetails.getUsername()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.folderName() + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.resource());
    }

    private static String parsePath(String requestURI) {
        return requestURI.substring("/objects".length());
    }
}
