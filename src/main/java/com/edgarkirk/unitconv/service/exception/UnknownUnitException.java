package com.edgarkirk.unitconv.service.exception;

public class UnknownUnitException extends ConversionException {

    public UnknownUnitException(String message) {
        this(message, null);
    }

    public UnknownUnitException(String message, String field) {
        super(message, field);
    }
}
