package com.edgarkirk.unitconv.service.exception;

public class UnitNotFoundException extends RuntimeException {

    private final String field;

    public UnitNotFoundException(String message) {
        this(message, null);
    }

    public UnitNotFoundException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
