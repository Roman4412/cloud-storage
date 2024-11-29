package com.pustovalov.cloudstorage.validation;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.validation.anotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequest> {

    @Override
    public boolean isValid(RegistrationRequest value, ConstraintValidatorContext context) {
        return Objects.equals(value.password(), value.confirmPassword());
    }
}