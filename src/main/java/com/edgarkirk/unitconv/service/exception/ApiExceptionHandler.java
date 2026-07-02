package com.edgarkirk.unitconv.service.exception;

import com.edgarkirk.unitconv.dto.response.ValidationError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
public class ApiExceptionHandler {

    @ExceptionHandler(ConversionValidationException.class)
    public ResponseEntity<ValidationError> handleConversionValidationException(ConversionValidationException exception) {
        return badRequest(exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        if (fieldError == null) {
            return badRequest("request body is invalid", null);
        }
        return badRequest(fieldError.getDefaultMessage(), fieldError.getField());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String field = extractField(exception);
        if ("value".equals(field)) {
            return badRequest("value must be numeric", field);
        }
        return badRequest("request body is invalid", field);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String field = exception.getName();
        if ("value".equals(field)) {
            return badRequest("value must be numeric", field);
        }
        return badRequest("request parameter is invalid", field);
    }

    private ResponseEntity<ValidationError> badRequest(String message, String field) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(UUID.randomUUID(), message, field));
    }

    private String extractField(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            return firstFieldName(invalidFormatException);
        }
        if (cause instanceof MismatchedInputException mismatchedInputException) {
            return firstFieldName(mismatchedInputException);
        }
        return null;
    }

    private String firstFieldName(MismatchedInputException exception) {
        return exception.getPath().isEmpty() ? null : exception.getPath().get(0).getFieldName();
    }
}
