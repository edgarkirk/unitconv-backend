package com.edgarkirk.unitconv.service.exception;

public class InvalidConversionException extends RuntimeException {

    private final String field;

    public InvalidConversionException(String message) {
        this(message, null);
    }

    public InvalidConversionException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
