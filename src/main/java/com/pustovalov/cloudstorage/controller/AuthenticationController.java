package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.LoginRequest;
import com.pustovalov.cloudstorage.security.JwtManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authManager;

    private final JwtManager jwtManager;

    @PostMapping
    public ResponseEntity<String> authenticate(@Validated @RequestBody @NotNull LoginRequest request) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(),
                                                                                               request.password()));
        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("Unexpected principal type");
        }

        String jwt = jwtManager.generateToken((UserDetails) principal);
        return ResponseEntity.ok(jwt);
    }

}
