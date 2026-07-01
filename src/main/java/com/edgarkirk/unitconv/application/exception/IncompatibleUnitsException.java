package com.edgarkirk.unitconv.application.exception;

public class IncompatibleUnitsException extends ConversionException {

    public IncompatibleUnitsException(String sourceUnit, String targetUnit) {
        super("Conversion between '" + sourceUnit + "' and '" + targetUnit + "' is not supported. Supported pairs are metres/feet, kilometres/miles, and litres/gallons.", null);
    }
}
