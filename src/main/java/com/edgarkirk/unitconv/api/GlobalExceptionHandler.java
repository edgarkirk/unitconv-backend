package com.edgarkirk.unitconv.api;

import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.response.ValidationError;
import com.edgarkirk.unitconv.service.exception.ConversionException;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.InvalidNumericValueException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
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

    @ExceptionHandler(IncompatibleUnitsException.class)
    public ResponseEntity<ValidationError> handleIncompatibleUnits(IncompatibleUnitsException exception) {
        return error(exception.getMessage().toLowerCase(java.util.Locale.ROOT), exception.getField());
    }

    @ExceptionHandler(UnknownUnitException.class)
    public ResponseEntity<ValidationError> handleUnknownUnit(UnknownUnitException exception) {
        return error(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(InvalidNumericValueException.class)
    public ResponseEntity<ValidationError> handleInvalidNumericValue(InvalidNumericValueException exception) {
        return error(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<ValidationError> handleConversionException(ConversionException exception) {
        return error(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return validationError(exception.getBindingResult().getFieldError());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationError> handleBindException(BindException exception) {
        return validationError(exception.getBindingResult().getFieldError());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof InvalidFormatException) {
            return error("invalid numeric value for field: value", "value");
        }
        return error("invalid request payload", null);
    }

    private ResponseEntity<ValidationError> validationError(FieldError fieldError) {
        String field = fieldError != null ? fieldError.getField() : null;
        String message = fieldError == null ? "invalid request payload" : messageFor(fieldError.getCode(), field);
        return error(message, field);
    }

    private ResponseEntity<ValidationError> error(String message, String field) {
        return ResponseEntity.badRequest().body(new ValidationError(UUID.randomUUID(), message, field));
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
