package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.exception.ConversionValidationException;
import com.edgarkirk.unitconv.dto.response.ValidationError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
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
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationError> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        final FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        final String field = fieldError == null ? null : fieldError.getField();
        final String message = fieldError == null || fieldError.getDefaultMessage() == null
                ? "Request body is invalid."
                : fieldError.getDefaultMessage();
        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationError> handleHttpMessageNotReadable(final HttpMessageNotReadableException exception) {
        final InvalidFormatException invalidFormatException = findCause(exception, InvalidFormatException.class);
        if (invalidFormatException != null) {
            final String field = invalidFormatException.getPath().isEmpty()
                    ? "value"
                    : invalidFormatException.getPath().get(0).getFieldName();
            return badRequest("Field '%s' must be numeric.".formatted(field), field);
        }
        return badRequest("Request body contains invalid input.", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ValidationError> handleConstraintViolation(final ConstraintViolationException exception) {
        final var violation = exception.getConstraintViolations().stream().findFirst().orElse(null);
        final String field = violation == null ? null : violation.getPropertyPath().toString();
        final String message = violation == null ? "Request violated a validation constraint." : violation.getMessage();
        return badRequest(message, field);
    }

    @ExceptionHandler(ConversionValidationException.class)
    ResponseEntity<ValidationError> handleConversionValidation(final ConversionValidationException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ValidationError> handleTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        return badRequest("Field '%s' has an invalid type.".formatted(exception.getName()), exception.getName());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ValidationError> handleUnexpected(final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ValidationError(UUID.randomUUID(), "An unexpected error occurred.", null));
    }

    private ResponseEntity<ValidationError> badRequest(final String message, final String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError(UUID.randomUUID(), message, field));
    }

    private <T extends Throwable> T findCause(final Throwable throwable, final Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}

