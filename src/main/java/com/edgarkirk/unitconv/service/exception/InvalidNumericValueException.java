package com.edgarkirk.unitconv.service.exception;

public class InvalidNumericValueException extends ConversionException {

    public InvalidNumericValueException(String message) {
        this(message, null);
    }

    public InvalidNumericValueException(String message, String field) {
        super(message, field);
    }
}
