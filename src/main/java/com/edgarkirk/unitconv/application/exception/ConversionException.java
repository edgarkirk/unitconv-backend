package com.edgarkirk.unitconv.application.exception;

public abstract class ConversionException extends RuntimeException {

    private final String field;

    protected ConversionException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
