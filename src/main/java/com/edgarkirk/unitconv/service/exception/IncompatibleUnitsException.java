package com.edgarkirk.unitconv.service.exception;

public class IncompatibleUnitsException extends ConversionValidationException {

    public IncompatibleUnitsException(String sourceUnit, String targetUnit) {
        super("Incompatible units: " + sourceUnit + " and " + targetUnit, null);
    }
}
