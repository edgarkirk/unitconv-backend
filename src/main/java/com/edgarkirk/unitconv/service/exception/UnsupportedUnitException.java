package com.edgarkirk.unitconv.service.exception;

public class UnsupportedUnitException extends RuntimeException {

    private final String field;

    public UnsupportedUnitException(String message) {
        this(message, null);
    }

    public UnsupportedUnitException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
