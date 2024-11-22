package com.pustovalov.cloudstorage.service.validation.anotations;

import com.pustovalov.cloudstorage.service.validation.validators.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "${user.registration.confirmPassword.matches}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
