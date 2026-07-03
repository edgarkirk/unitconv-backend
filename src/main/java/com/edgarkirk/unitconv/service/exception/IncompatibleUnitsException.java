package com.edgarkirk.unitconv.service.exception;

public class IncompatibleUnitsException extends ConversionException {

    public IncompatibleUnitsException(String message) {
        this(message, null);
    }

    public IncompatibleUnitsException(String message, String field) {
        super(message, field);
    }
}
