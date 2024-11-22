package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> register(@Validated @RequestBody @NotNull RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userService.register(request));
    }

}