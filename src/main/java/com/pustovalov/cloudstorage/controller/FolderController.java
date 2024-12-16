package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.FolderCreateRequest;
import com.pustovalov.cloudstorage.dto.request.FolderDeleteRequest;
import com.pustovalov.cloudstorage.dto.request.FolderGetContentRequest;
import com.pustovalov.cloudstorage.dto.request.FolderRenameRequest;
import com.pustovalov.cloudstorage.dto.response.FolderGetContentResponse;
import com.pustovalov.cloudstorage.service.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/**")
    public ResponseEntity<String> create(@RequestParam
                                         @Size(min = 1, max = 255, message = "{folder.create.name.size}")
                                         @Pattern(regexp = "^[a-zA-Z0-9_.\\-()\\[\\]]+$", message = "{folder.create.name.pattern}")
                                         String name,
                                         @AuthenticationPrincipal
                                         UserDetails userDetails,
                                         HttpServletRequest request) {

        FolderCreateRequest dto = new FolderCreateRequest(parsePath(request.getRequestURI()), name, userDetails.getUsername());
        folderService.createFolder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/**")
    public ResponseEntity<FolderGetContentResponse> getContent(@AuthenticationPrincipal UserDetails userDetails,
                                                               HttpServletRequest request) {
        FolderGetContentRequest dto = new FolderGetContentRequest(parsePath(request.getRequestURI()), userDetails.getUsername());
        FolderGetContentResponse response = folderService.getContent(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/**")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserDetails userDetails,
                                         HttpServletRequest request) {
        folderService.delete(new FolderDeleteRequest(parsePath(request.getRequestURI()), userDetails.getUsername()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/**")
    public ResponseEntity<String> rename(@RequestParam
                                         @Size(min = 1, max = 255, message = "{folder.create.name.size}")
                                         @Pattern(regexp = "^[a-zA-Z0-9_.\\-()\\[\\]]+$", message = "{folder.create.name.pattern}")
                                         String newName,
                                         @AuthenticationPrincipal
                                         UserDetails userDetails,
                                         HttpServletRequest request) {
        FolderRenameRequest dto = FolderRenameRequest.builder()
                .path(parsePath(request.getRequestURI()))
                .newName(newName)
                .username(userDetails.getUsername())
                .build();
        folderService.rename(dto);
        return ResponseEntity.noContent().build();
    }

    private static String parsePath(String requestURI) {
        return requestURI.substring("/folders".length());
    }

}
