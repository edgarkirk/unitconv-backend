package com.edgarkirk.unitconv.api;

import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.ConversionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<ValidationError> handleConversionException(ConversionException exception) {
        return ResponseEntity.badRequest().body(new ValidationError(UUID.randomUUID(), exception.getMessage().toLowerCase(java.util.Locale.ROOT), exception.getField()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : null;
        String message = fieldError == null ? "Invalid request payload" : messageFor(fieldError.getCode(), field);
        return ResponseEntity.badRequest().body(new ValidationError(UUID.randomUUID(), message, field));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationError> handleBindException(BindException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : null;
        String message = fieldError == null ? "Invalid request payload" : messageFor(fieldError.getCode(), field);
        return ResponseEntity.badRequest().body(new ValidationError(UUID.randomUUID(), message, field));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof InvalidFormatException) {
            return ResponseEntity.badRequest().body(new ValidationError(
                    UUID.randomUUID(),
                    "invalid numeric value for field: value",
                    "value"));
        }
        return ResponseEntity.badRequest().body(new ValidationError(UUID.randomUUID(), "invalid request payload", null));
    }

    private String messageFor(String code, String field) {
        if ("NotNull".equals(code) || "NotBlank".equals(code)) {
            return "missing required field: " + field;
        }
        if ("InvalidFormat".equals(code)) {
            return "invalid numeric value for field: " + field;
        }
        return "invalid value for field: " + field;
    }
}
