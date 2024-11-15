package com.pustovalov.cloudstorage.service.validation.validators;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationData;
import com.pustovalov.cloudstorage.service.validation.anotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationData> {

    @Override
    public boolean isValid(UserRegistrationData value, ConstraintValidatorContext context) {
        return Objects.equals(value.getPassword(), value.getConfirmPassword());
    }
}