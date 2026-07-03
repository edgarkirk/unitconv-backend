package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.response.ValidationErrorResponse;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.InvalidConversionException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@Validated
class ConversionExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError == null ? null : fieldError.getField();
        String message = fieldError == null ? "Invalid request content" : fieldError.getDefaultMessage();
        return badRequest(message, field);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        String field = extractFieldName(exception);
        String message = field == null ? "Invalid request body" : "Invalid numeric value for field " + field;
        return badRequest(message, field);
    }

    @ExceptionHandler({IncompatibleUnitsException.class, InvalidConversionException.class, UnsupportedUnitException.class})
    ResponseEntity<ValidationErrorResponse> handleDomainException(RuntimeException exception) {
        String field = null;
        if (exception instanceof IncompatibleUnitsException incompatibleUnitsException) {
            field = defaultField(incompatibleUnitsException.getField(), "sourceUnit");
        } else if (exception instanceof InvalidConversionException invalidConversionException) {
            field = defaultField(invalidConversionException.getField(), "sourceUnit");
        } else if (exception instanceof UnsupportedUnitException unsupportedUnitException) {
            field = defaultField(unsupportedUnitException.getField(), "sourceUnit");
        }
        return badRequest(exception.getMessage(), field);
    }

    private ResponseEntity<ValidationErrorResponse> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(UUID.randomUUID(), message, field));
    }

    private String defaultField(String field, String defaultField) {
        return field == null ? defaultField : field;
    }

    private String extractFieldName(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            return invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst()
                    .orElse("value");
        }
        if (cause instanceof JsonMappingException jsonMappingException) {
            return jsonMappingException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst()
                    .orElse("value");
        }
        return null;
    }
}
