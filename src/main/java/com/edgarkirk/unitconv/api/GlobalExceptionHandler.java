package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError != null ? fieldError.getField() : null;
        Object rejectedValue = fieldError != null ? fieldError.getRejectedValue() : null;
        String message;

        if (fieldError == null) {
            message = "Validation failed";
        } else if (rejectedValue == null) {
            message = "Missing required field: " + field;
        } else if (rejectedValue instanceof String stringValue && stringValue.isBlank()) {
            message = "Invalid " + field + ": must not be blank";
        } else {
            message = fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid " + field;
        }

        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        if (containsInvalidFormatException(exception)) {
            return badRequest("Invalid value: expected a numeric JSON number", "value");
        }

        return badRequest("Invalid request body", null);
    }

    @ExceptionHandler(UnknownUnitException.class)
    public ResponseEntity<ValidationError> handleUnknownUnit(UnknownUnitException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(IncompatibleUnitsException.class)
    public ResponseEntity<ValidationError> handleIncompatibleUnits(IncompatibleUnitsException exception) {
        return badRequest(exception.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationError> handleIllegalArgument(IllegalArgumentException exception) {
        return badRequest(exception.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return badRequest("Invalid request parameter", exception.getName());
    }

    private boolean containsInvalidFormatException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof InvalidFormatException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }
}
