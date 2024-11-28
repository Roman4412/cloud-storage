package com.pustovalov.cloudstorage.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        Collector<FieldError, ?, List<String>> fieldErrorMessages = Collectors.mapping(FieldError::getDefaultMessage,
                                                                                       Collectors.toList());
        Map<String, List<String>> errorsGroupByFields = bindingResult.getFieldErrors()
                                                                     .stream()
                                                                     .collect(Collectors.groupingBy(FieldError::getField,
                                                                                                    fieldErrorMessages));

        log.info("{} - {} - validation errors: {}",
                 e.getClass().getName(),
                 detail,
                 errorsGroupByFields);

        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("errors", errorsGroupByFields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleOtherExceptions(Exception e) {
        String detail = "unexpected server error";
        log.error("{}", detail, e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException e) {
        String detail = "invalid username or password";
        log.info("{} - {} ", e.getClass().getName(), detail);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(createProblemDetail(HttpStatus.UNAUTHORIZED, detail));
    }

    @ExceptionHandler(ObjectAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleObjectAlreadyExistException(ObjectAlreadyExistException e) {
        String detail = "such a user has already been registered";
        log.error("{}", detail, e);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(createProblemDetail(HttpStatus.CONFLICT, detail));
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

}