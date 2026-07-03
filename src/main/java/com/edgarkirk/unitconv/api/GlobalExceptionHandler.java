package com.edgarkirk.unitconv.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError == null ? null : fieldError.getField();
        String message = fieldError == null ? "Invalid request payload" : fieldError.getDefaultMessage();
        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException && !invalidFormatException.getPath().isEmpty()) {
            String field = invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1).getFieldName();
            if ("value".equals(field)) {
                return badRequest("Invalid numeric value", field);
            }
            return badRequest("Invalid value for field: " + field, field);
        }
        return badRequest("Invalid request payload", null);
    }

    @ExceptionHandler({UnsupportedUnitException.class, IncompatibleUnitException.class})
    public ResponseEntity<ValidationError> handleIllegalArgument(IllegalArgumentException exception) {
        return badRequest(exception.getMessage(), null);
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }
}
