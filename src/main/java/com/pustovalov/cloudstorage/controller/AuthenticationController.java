package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.UserLoginRequest;
import com.pustovalov.cloudstorage.dto.request.UserRegistrationRequest;
import com.pustovalov.cloudstorage.security.JwtManager;
import com.pustovalov.cloudstorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    private final AuthenticationManager authManager;

    private final JwtManager jwtManager;

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody UserLoginRequest request) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtManager.generateToken((UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Validated @RequestBody UserRegistrationRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
