package com.edgarkirk.unitconv.service.exception;

public class ConversionException extends RuntimeException {

    private final String field;

    public ConversionException(String message) {
        this(message, null);
    }

    public ConversionException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
