package com.edgarkirk.unitconv.application.exception;

public class UnsupportedUnitException extends ConversionException {

    public UnsupportedUnitException(String field, String unit) {
        super(buildMessage(field, unit), field);
    }

    private static String buildMessage(String field, String unit) {
        return "Unsupported " + field + " '" + unit + "'. Supported units are metres, feet, kilometres, miles, litres, and gallons.";
    }
}
