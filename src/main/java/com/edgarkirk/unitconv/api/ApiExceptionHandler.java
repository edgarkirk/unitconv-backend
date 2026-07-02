package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.InvalidConversionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        return badRequest(fieldError == null ? "Request validation failed" : fieldError.getDefaultMessage(),
                fieldError == null ? null : fieldError.getField());
    }

    @ExceptionHandler(BindException.class)
    ResponseEntity<ValidationError> handleBindException(BindException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        return badRequest(fieldError == null ? "Request validation failed" : fieldError.getDefaultMessage(),
                fieldError == null ? null : fieldError.getField());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);
        if (rootCause instanceof InvalidFormatException invalidFormatException && isNumericTarget(invalidFormatException.getTargetType())) {
            String field = invalidFormatException.getPath().stream()
                    .map(reference -> reference.getFieldName())
                    .filter(name -> name != null && !name.isBlank())
                    .reduce((first, second) -> second)
                    .orElse("value");
            return badRequest("Invalid numeric value: " + field + " must be a number", field);
        }
        return badRequest("Invalid request body: unable to parse conversion request", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationError> handleConstraintViolationException(ConstraintViolationException exception) {
        String field = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() == null ? null : violation.getPropertyPath().toString())
                .filter(path -> path != null && !path.isBlank())
                .map(path -> path.substring(path.lastIndexOf('.') + 1))
                .findFirst()
                .orElse(null);
        String message = exception.getConstraintViolations().stream()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Request validation failed");
        return badRequest(message, field);
    }

    @ExceptionHandler(InvalidConversionException.class)
    ResponseEntity<ValidationError> handleInvalidConversionException(InvalidConversionException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }

    private boolean isNumericTarget(Class<?> targetType) {
        return targetType != null && (Number.class.isAssignableFrom(targetType) || targetType == java.math.BigDecimal.class);
    }
}
