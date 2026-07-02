package com.edgarkirk.unitconv.service.exception;

public class UnsupportedUnitException extends ConversionValidationException {

    public UnsupportedUnitException(String unitName, String field) {
        super("Unsupported unit: " + unitName, field);
    }
}
