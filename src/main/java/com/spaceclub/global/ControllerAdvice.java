package com.spaceclub.global;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @Order(1)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<String> illegalStateException(IllegalStateException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @Order(3)
    @ExceptionHandler
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }



}
