package com.pustovalov.cloudstorage.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
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
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                               BindingResult bindingResult) {
        String detail = "invalid data has been transmitted";
        Collector<FieldError, ?, List<String>> fieldErrorMessages =
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList());

        Map<String, List<String>> errorsGroupByFields =
                bindingResult.getFieldErrors()
                             .stream()
                             .collect(Collectors.groupingBy(FieldError::getField, fieldErrorMessages));

        log.info("{} - {} - validation errors: {}",
                 e.getClass().getName(),
                 detail,
                 errorsGroupByFields);

        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("errors", errorsGroupByFields);
        return ResponseEntity.of(pd).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException e) {
        String detail = "invalid username or password";
        log.info("{} - {} ", e.getClass().getName(), detail);
        return ResponseEntity.of(createProblemDetail(HttpStatus.UNAUTHORIZED, detail))
                             .build();
    }

    @ExceptionHandler(ObjectAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleObjectAlreadyExistException(ObjectAlreadyExistException e) {
        String detail = "such a object has already exist";
        log.error("{}", detail, e);
        return ResponseEntity.of(createProblemDetail(HttpStatus.CONFLICT, detail))
                             .build();
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleObjectNotFoundException(ObjectNotFoundException e) {
        String detail = "object not found";
        log.error("{}", detail, e);
        return ResponseEntity.of(createProblemDetail(HttpStatus.NOT_FOUND, detail))
                .build();
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

}