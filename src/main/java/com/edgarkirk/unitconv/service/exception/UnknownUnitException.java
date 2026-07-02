package com.edgarkirk.unitconv.service.exception;

public class UnknownUnitException extends RuntimeException {

    private final String field;

    public UnknownUnitException(String message, String field) {
        super(message);
        this.field = field;
    }

    public UnknownUnitException(String message) {
        this(message, null);
    }

    public String getField() {
        return field;
    }
}
