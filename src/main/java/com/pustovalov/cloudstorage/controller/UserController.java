package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.service.FolderService;
import com.pustovalov.cloudstorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<?> register(@Validated @RequestBody RegistrationRequest request) {
        User createdUser = userService.register(request);
        folderService.createPersonalFolder(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .build();
    }

}