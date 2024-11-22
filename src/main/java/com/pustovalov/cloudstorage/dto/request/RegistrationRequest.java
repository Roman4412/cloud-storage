package com.pustovalov.cloudstorage.dto.request;

import com.pustovalov.cloudstorage.validation.anotations.PasswordMatches;
import jakarta.validation.constraints.Pattern;

@PasswordMatches
public record RegistrationRequest(
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{user.registration.login.regex}")
        String username,

        @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{user.registration.password.regex}")
        String password,

        @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{user.registration.password.regex}")
        String confirmPassword) {

}