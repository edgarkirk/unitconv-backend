package com.edgarkirk.unitconv.service.exception;

public class UnknownUnitException extends RuntimeException {

    private final String field;

    public UnknownUnitException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
