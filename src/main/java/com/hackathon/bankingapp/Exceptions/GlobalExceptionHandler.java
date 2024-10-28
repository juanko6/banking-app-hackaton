package com.hackathon.bankingapp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public Map<String, String> handleDuplicateUser(DuplicateUserException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public Map<String, String> handleInvalidPassword(InvalidPasswordException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public Map<String, String> handleInvalidEmail(InvalidEmailException ex) {
        return Map.of("error", ex.getMessage());
    }
}
