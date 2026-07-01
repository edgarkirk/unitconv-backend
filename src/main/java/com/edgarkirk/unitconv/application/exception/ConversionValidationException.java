package com.edgarkirk.unitconv.application.exception;

public class ConversionValidationException extends RuntimeException {

    private final String field;

    public ConversionValidationException(final String message) {
        this(message, null);
    }

    public ConversionValidationException(final String message, final String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
