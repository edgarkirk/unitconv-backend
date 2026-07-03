package com.edgarkirk.unitconv.config;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        if (fieldError == null) {
            return validationError("Invalid request", null);
        }
        String message = fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Missing required field: " + fieldError.getField();
        return validationError(message, fieldError.getField());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return validationError("Invalid numeric value", null);
    }

    @ExceptionHandler(UnsupportedUnitException.class)
    public ResponseEntity<ValidationError> handleUnsupportedUnit(UnsupportedUnitException exception) {
        return validationError(exception.getMessage(), null);
    }

    @ExceptionHandler(IncompatibleUnitException.class)
    public ResponseEntity<ValidationError> handleIncompatibleUnit(IncompatibleUnitException exception) {
        return validationError(exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationError> handleUnexpected(Exception exception) {
        return validationError("Unexpected error", null);
    }

    private ResponseEntity<ValidationError> validationError(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError(UUID.randomUUID(), message, field));
    }
}
