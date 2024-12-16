package com.pustovalov.cloudstorage.validation;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationRequest;
import com.pustovalov.cloudstorage.validation.anotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationRequest> {

    @Override
    public boolean isValid(UserRegistrationRequest value, ConstraintValidatorContext context) {
        return Objects.equals(value.password(), value.confirmPassword());
    }
}