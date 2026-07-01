package com.example.unitconv.api.advice;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.example.unitconv.api.response.ErrorResponse;
import com.example.unitconv.application.exception.IncompatibleUnitsException;
import com.example.unitconv.application.exception.InvalidInputException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String field = firstField(exception.getFieldErrors());
        return badRequest(new ErrorResponse(UUID.randomUUID(), "Missing required field: " + field, field));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = invalidFormatException.getPath().isEmpty()
                ? "value"
                : invalidFormatException.getPath().getFirst().getFieldName();
            if ("value".equals(field)) {
                return badRequest(new ErrorResponse(UUID.randomUUID(), "Invalid input: value must be numeric", field));
            }
            return badRequest(new ErrorResponse(UUID.randomUUID(), "Invalid input: " + field + " has an invalid format", field));
        }
        return badRequest(new ErrorResponse(UUID.randomUUID(), "Invalid input: request body is malformed", null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String field = exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath().toString())
            .map(path -> path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path)
            .sorted(Comparator.naturalOrder())
            .findFirst()
            .orElse("value");
        return badRequest(new ErrorResponse(UUID.randomUUID(), "Missing required field: " + field, field));
    }

    @ExceptionHandler(InvalidInputException.class)
    ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException exception) {
        return badRequest(new ErrorResponse(UUID.randomUUID(), exception.getMessage(), null));
    }

    @ExceptionHandler(IncompatibleUnitsException.class)
    ResponseEntity<ErrorResponse> handleIncompatibleUnits(IncompatibleUnitsException exception) {
        return badRequest(new ErrorResponse(UUID.randomUUID(), exception.getMessage(), null));
    }

    private ResponseEntity<ErrorResponse> badRequest(ErrorResponse errorResponse) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private String firstField(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
            .map(FieldError::getField)
            .min(Comparator.comparingInt(this::priority))
            .orElse("value");
    }

    private int priority(String field) {
        return switch (field) {
            case "id" -> 0;
            case "value" -> 1;
            case "sourceUnit" -> 2;
            case "targetUnit" -> 3;
            default -> 10;
        };
    }
}
