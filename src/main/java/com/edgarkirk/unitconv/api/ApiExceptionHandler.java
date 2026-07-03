package com.edgarkirk.unitconv.api;

import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.response.ValidationErrorResponse;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitPairException;
import com.edgarkirk.unitconv.service.exception.UnitNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Validated
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError == null ? "Invalid request" : fieldError.getDefaultMessage();
        String field = fieldError == null ? null : fieldError.getField();
        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = invalidFormatException.getPath().stream()
                    .findFirst()
                    .map(reference -> reference.getFieldName())
                    .orElse("value");
            return badRequest("value must be numeric", field);
        }
        if (cause instanceof JsonParseException) {
            return badRequest("Invalid JSON payload", null);
        }
        return badRequest("Invalid JSON payload", null);
    }

    @ExceptionHandler(UnitNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleUnitNotFound(UnitNotFoundException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(IncompatibleUnitPairException.class)
    public ResponseEntity<ValidationErrorResponse> handleIncompatibleUnitPair(IncompatibleUnitPairException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    private ResponseEntity<ValidationErrorResponse> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(UUID.randomUUID(), message, field));
    }
}
