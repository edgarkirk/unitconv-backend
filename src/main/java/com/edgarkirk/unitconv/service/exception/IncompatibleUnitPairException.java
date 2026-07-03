package com.edgarkirk.unitconv.service.exception;

public class IncompatibleUnitPairException extends RuntimeException {

    private final String field;

    public IncompatibleUnitPairException(String message) {
        this(message, null);
    }

    public IncompatibleUnitPairException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
