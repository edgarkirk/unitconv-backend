package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
class DefaultConversionService implements ConversionService {

    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal FEET_TO_METRES = new BigDecimal("0.3048");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal MILES_TO_KILOMETRES = new BigDecimal("1.60934");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");
    private static final BigDecimal GALLONS_TO_LITRES = new BigDecimal("3.78541");

    private static final Map<String, String> UNIT_GROUPS = Map.of(
            "metres", "length",
            "feet", "length",
            "kilometres", "length",
            "miles", "length",
            "litres", "volume",
            "gallons", "volume");

    private final UnitCatalog unitCatalog;

    DefaultConversionService(UnitCatalog unitCatalog) {
        this.unitCatalog = unitCatalog;
    }

    @Override
    public ConversionResult convert(ConversionRequest request) {
        unitCatalog.supportedUnits();

        String sourceUnit = request.sourceUnit();
        String targetUnit = request.targetUnit();
        validateSupportedUnit(sourceUnit);
        validateSupportedUnit(targetUnit);

        BigDecimal result = sourceUnit.equals(targetUnit)
                ? request.value()
                : convertBetweenSupportedUnits(request.value(), sourceUnit, targetUnit);

        return new ConversionResult(request.id(), request.value(), sourceUnit, targetUnit, result);
    }

    private static void validateSupportedUnit(String unit) {
        if (!UNIT_GROUPS.containsKey(unit)) {
            throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    private static BigDecimal convertBetweenSupportedUnits(BigDecimal value, String sourceUnit, String targetUnit) {
        if (!UNIT_GROUPS.get(sourceUnit).equals(UNIT_GROUPS.get(targetUnit))) {
            throw new IllegalArgumentException("Incompatible units: " + sourceUnit + " to " + targetUnit);
        }

        return switch (sourceUnit + "->" + targetUnit) {
            case "metres->feet" -> value.multiply(METRES_TO_FEET);
            case "feet->metres" -> value.multiply(FEET_TO_METRES);
            case "kilometres->miles" -> value.multiply(KILOMETRES_TO_MILES);
            case "miles->kilometres" -> value.multiply(MILES_TO_KILOMETRES);
            case "litres->gallons" -> value.multiply(LITRES_TO_GALLONS);
            case "gallons->litres" -> value.multiply(GALLONS_TO_LITRES);
            default -> throw new IllegalArgumentException("Incompatible units: " + sourceUnit + " to " + targetUnit);
        };
    }
}
