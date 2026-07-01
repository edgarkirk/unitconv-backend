package com.edgarkirk.unitconv.application;

public class ConversionValidationException extends RuntimeException {

    private final String field;

    public ConversionValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
