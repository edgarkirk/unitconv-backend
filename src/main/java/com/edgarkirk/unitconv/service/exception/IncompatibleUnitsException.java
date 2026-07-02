package com.edgarkirk.unitconv.service.exception;

public class IncompatibleUnitsException extends InvalidConversionException {

    public IncompatibleUnitsException(String sourceUnit, String targetUnit) {
        super("Incompatible units: " + sourceUnit + " cannot be converted to " + targetUnit);
    }
}
