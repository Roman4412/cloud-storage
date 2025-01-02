package com.pustovalov.cloudstorage.dto.request;

import com.pustovalov.cloudstorage.validation.anotations.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@PasswordMatches
public record UserRegistrationRequest(
        @NotNull(message = "{username.registration.notnull}")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{username.registration.login.regex}")
        String username,

        @NotNull(message = "{username.registration.notnull}")
        @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{username.registration.password.regex}")
        String password,

        @NotNull(message = "{username.registration.notnull}")
        @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{username.registration.password.regex}")
        String confirmPassword) {

}