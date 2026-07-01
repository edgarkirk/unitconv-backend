package com.edgarkirk.unitconv.api.advice;

import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.response.ValidationError;
import com.edgarkirk.unitconv.application.exception.ConversionException;
import com.edgarkirk.unitconv.application.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.application.exception.UnsupportedUnitException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .distinct()
                .sorted()
                .toList();
        String field = fields.size() == 1 ? fields.getFirst() : null;
        String message = fields.size() == 1
                ? "Request field '" + fields.getFirst() + "' is required."
                : "Request fields " + String.join(", ", fields) + " are required.";
        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        String field = extractJsonField(exception);
        if (field != null) {
            return badRequest("Request field '" + field + "' must be numeric.", field);
        }
        return badRequest("Request body contains an invalid numeric value.", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationError> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> "Request parameter validation failed: " + violation.getMessage())
                .orElse("Request parameter validation failed.");
        return badRequest(message, null);
    }

    @ExceptionHandler(UnsupportedUnitException.class)
    ResponseEntity<ValidationError> handleUnsupportedUnit(UnsupportedUnitException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(IncompatibleUnitsException.class)
    ResponseEntity<ValidationError> handleIncompatibleUnits(IncompatibleUnitsException exception) {
        return badRequest(exception.getMessage(), null);
    }

    @ExceptionHandler(ConversionException.class)
    ResponseEntity<ValidationError> handleConversionException(ConversionException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    private String extractJsonField(HttpMessageNotReadableException exception) {
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        if (mostSpecificCause instanceof JsonMappingException jsonMappingException && !jsonMappingException.getPath().isEmpty()) {
            return jsonMappingException.getPath().getFirst().getFieldName();
        }
        return null;
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }
}
