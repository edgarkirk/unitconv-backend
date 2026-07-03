package com.edgarkirk.unitconv.config;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return validationErrorFromFieldError(exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationError> handleBindException(BindException exception) {
        return validationErrorFromFieldError(exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        if (isBatchPayload(exception)) {
            return validationError("Batch conversions are not supported", null, HttpStatus.BAD_REQUEST);
        }
        return validationError("Invalid numeric value", null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedUnitException.class)
    public ResponseEntity<ValidationError> handleUnsupportedUnit(UnsupportedUnitException exception) {
        return validationError(exception.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompatibleUnitException.class)
    public ResponseEntity<ValidationError> handleIncompatibleUnit(IncompatibleUnitException exception) {
        return validationError(exception.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ValidationError> handleNoResourceFound(NoResourceFoundException exception) {
        return validationError("Resource not found", null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationError> handleUnexpected(Exception exception) {
        return validationError("Unexpected error", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ValidationError> validationErrorFromFieldError(FieldError fieldError) {
        if (fieldError == null) {
            return validationError("Invalid request", null, HttpStatus.BAD_REQUEST);
        }
        String message = fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Missing required field: " + fieldError.getField();
        return validationError(message, fieldError.getField(), HttpStatus.BAD_REQUEST);
    }

    private boolean isBatchPayload(HttpMessageNotReadableException exception) {
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        String message = exception.getMessage();
        return mostSpecificCause instanceof MismatchedInputException
                && message != null
                && (message.contains("START_ARRAY") || message.contains("from Array value") || message.contains("Cannot deserialize value of type") && message.contains("ConversionRequest"));
    }

    private ResponseEntity<ValidationError> validationError(String message, String field, HttpStatus status) {
        return ResponseEntity.status(status).body(new ValidationError(UUID.randomUUID(), message, field));
    }
}
