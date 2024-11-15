package com.pustovalov.cloudstorage.dto.request;

import com.pustovalov.cloudstorage.service.validation.anotations.PasswordMatches;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PasswordMatches
public class UserRegistrationData {

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{user.registration.login.regex}")
    private String login;

    @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{user.registration.password.regex}")
    private String password;

    @Pattern(regexp = "^[A-Za-z\\d]{8,}$", message = "{user.registration.password.regex}")
    private String confirmPassword;

}