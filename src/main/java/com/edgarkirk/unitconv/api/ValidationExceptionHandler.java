package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.ConversionValidationException;
import com.edgarkirk.unitconv.dto.response.ValidationError;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = findFieldError(exception, "value", "sourceUnit", "targetUnit");
        if (fieldError == null) {
            fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        }
        if (fieldError == null) {
            return badRequest("Request validation failed.", null);
        }
        return badRequest(fieldError.getDefaultMessage(), fieldError.getField());
    }

    private FieldError findFieldError(MethodArgumentNotValidException exception, String... preferredFields) {
        for (String preferredField : preferredFields) {
            for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
                if (preferredField.equals(fieldError.getField())) {
                    return fieldError;
                }
            }
        }
        return null;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        String field = extractFieldName(exception);
        if (field != null) {
            return badRequest(field + " must be numeric.", field);
        }
        return badRequest("Request body is malformed.", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationError> handleConstraintViolation(ConstraintViolationException exception) {
        var violation = exception.getConstraintViolations().stream().findFirst().orElse(null);
        if (violation == null) {
            return badRequest("Request validation failed.", null);
        }
        String field = violation.getPropertyPath().toString();
        if (field.contains(".")) {
            field = field.substring(field.lastIndexOf('.') + 1);
        }
        return badRequest(violation.getMessage(), field.isBlank() ? null : field);
    }

    @ExceptionHandler(ConversionValidationException.class)
    ResponseEntity<ValidationError> handleConversionValidation(ConversionValidationException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }

    private String extractFieldName(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof InvalidFormatException invalidFormatException) {
                return extractPath(invalidFormatException.getPath());
            }
            if (cause instanceof MismatchedInputException mismatchedInputException) {
                return extractPath(mismatchedInputException.getPath());
            }
            cause = cause.getCause();
        }
        return null;
    }

    private String extractPath(List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        return path.getFirst().getFieldName();
    }
}
