package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.ValidationError;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError == null ? null : fieldError.getField();
        String message = field == null ? "Invalid request" : "Missing required field: " + field;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), "Invalid numeric value", null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationError> handleConstraintViolation(ConstraintViolationException exception) {
        String field = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .findFirst()
                .orElse(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), "Invalid input", field));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ValidationError> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), exception.getMessage(), null));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    ResponseEntity<ValidationError> handleUnsupportedOperation(UnsupportedOperationException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ValidationError(UUID.randomUUID(), exception.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ValidationError> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ValidationError(UUID.randomUUID(), "Unexpected server error", null));
    }
}
