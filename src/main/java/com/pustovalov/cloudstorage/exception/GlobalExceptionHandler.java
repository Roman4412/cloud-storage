package com.pustovalov.cloudstorage.exception;

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

@RestControllerAdvice
public class GlobalExceptionHandler {
    //TODO добавить логирование

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                               BindingResult bindingResult) {
        e.printStackTrace();
        Collector<FieldError, ?, List<String>> fieldErrorMessages = Collectors.mapping(FieldError::getDefaultMessage,
                                                                                       Collectors.toList());
        Map<String, List<String>> errorsGroupByFields = bindingResult.getFieldErrors()
                                                                     .stream()
                                                                     .collect(Collectors.groupingBy(FieldError::getField,
                                                                                                    fieldErrorMessages));

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail("The received data has not been validated");
        pd.setProperty("errors", errorsGroupByFields);
        pd.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleOtherExceptions(Exception e) {
        e.printStackTrace();
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setDetail("Unexpected server error");
        pd.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(pd);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException e) {
        e.printStackTrace();
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setDetail("invalid username or password");
        pd.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(pd);
    }

    @ExceptionHandler(ObjectAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleObjectAlreadyExistException(ObjectAlreadyExistException e) {
        e.printStackTrace();
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setDetail("such a user has already been registered");
        pd.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(pd);
    }

}