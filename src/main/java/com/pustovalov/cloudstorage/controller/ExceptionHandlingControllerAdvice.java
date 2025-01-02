package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.exception.IllegalActionException;
import com.pustovalov.cloudstorage.exception.ObjectCreationException;
import com.pustovalov.cloudstorage.exception.ObjectNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String detail = "The transmitted data is invalid";
        BindingResult bindingResult = e.getBindingResult();
        Collector<FieldError, ?, List<String>> fieldErrorMessages =
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList());

        Map<String, List<String>> errorsGroupByFields =
                bindingResult.getFieldErrors()
                             .stream()
                             .collect(Collectors.groupingBy(FieldError::getField, fieldErrorMessages));

        log.warn("{} - {} - validation errors: {}",
                 e.getClass().getName(),
                 detail,
                 errorsGroupByFields);

        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("errors", errorsGroupByFields);

        return ResponseEntity.of(pd).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException e) {
        String detail = "The transmitted data is invalid";
        List<String> validationMessages = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        log.warn("{} - {} - validation errors: {}", e.getClass().getName(), detail, validationMessages);

        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("validation errors", validationMessages);

        return ResponseEntity.of(pd).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException e) {
        String detail = "invalid username or password";
        log.info("{} - {} ", e.getClass().getName(), detail);
        return ResponseEntity.of(createProblemDetail(HttpStatus.UNAUTHORIZED, detail))
                             .build();
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleObjectNotFoundException(ObjectNotFoundException e) {
        String message = e.getMessage();
        log.error("{}", message, e);
        return ResponseEntity.of(createProblemDetail(HttpStatus.NOT_FOUND, message))
                .build();
    }

    @ExceptionHandler(ObjectCreationException.class)
    public ResponseEntity<ProblemDetail> handleObjectCreationException(ObjectCreationException e) {
        log.error("{}", e.getMessage(), e);
        return ResponseEntity.of(createProblemDetail(HttpStatus.CONFLICT, e.getMessage()))
                .build();
    }

    @ExceptionHandler(IllegalActionException.class)
    public ResponseEntity<ProblemDetail> handleIllegalActionException(IllegalActionException e) {
        log.error("{}", e.getMessage(), e);
        return ResponseEntity.of(createProblemDetail(HttpStatus.FORBIDDEN, e.getMessage()))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred: {}", e.getMessage(), e);
        List<String> uniqueConstraints = List.of("s3objects_unique_object_key_idx",
                                                 "users_unique_username_idx");
        HttpStatus status = null;
        String detail = "";
        if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException cause) {
            if (uniqueConstraints.contains(cause.getConstraintName())) {
                status = HttpStatus.CONFLICT;
                detail = "Such an object already exists";
            }
            return ResponseEntity.of(createProblemDetail(status, detail))
                    .build();
        }
        throw e;
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

}