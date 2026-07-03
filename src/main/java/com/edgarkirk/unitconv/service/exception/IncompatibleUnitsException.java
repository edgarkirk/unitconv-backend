package com.edgarkirk.unitconv.service.exception;

public class IncompatibleUnitsException extends RuntimeException {

    private final String field;

    public IncompatibleUnitsException(String message) {
        this(message, null);
    }

    public IncompatibleUnitsException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
