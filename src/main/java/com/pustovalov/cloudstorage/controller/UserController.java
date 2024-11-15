package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationData;
import com.pustovalov.cloudstorage.exception.ObjectAlreadyExistException;
import com.pustovalov.cloudstorage.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationData data) {
        userService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .build();
    }

    @ExceptionHandler(ObjectAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleObjectAlreadyExistException(ObjectAlreadyExistException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(BindingResult bindingResult) {
        Map<String, List<String>> errors = bindingResult.getFieldErrors().stream()
                                                        .collect(Collectors.groupingBy(FieldError::getField,
                                                                                       Collectors.mapping(FieldError::getDefaultMessage,
                                                                                                          Collectors.toList())));

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail("The received data has not been validated");
        pd.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(pd);
    }
}