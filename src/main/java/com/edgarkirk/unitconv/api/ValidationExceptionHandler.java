package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.ValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        throw new UnsupportedOperationException("Validation handling is not implemented yet");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ValidationError> handleIllegalArgument(IllegalArgumentException exception) {
        throw new UnsupportedOperationException("Validation handling is not implemented yet");
    }
}
