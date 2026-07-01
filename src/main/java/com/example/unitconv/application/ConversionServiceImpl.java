package com.example.unitconv.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import com.example.unitconv.api.request.ConversionRequest;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.api.response.UnitResponse;
import com.example.unitconv.application.exception.IncompatibleUnitsException;
import com.example.unitconv.application.exception.InvalidInputException;
import org.springframework.stereotype.Service;

@Service
class ConversionServiceImpl implements ConversionService {

    private static final int SCALE = 10;

    private final UnitCatalog unitCatalog;

    ConversionServiceImpl(UnitCatalog unitCatalog) {
        this.unitCatalog = unitCatalog;
    }

    @Override
    public ConversionResult convert(ConversionRequest request) {
        UnitDefinition sourceUnit = unit(request.sourceUnit());
        UnitDefinition targetUnit = unit(request.targetUnit());

        if (!sourceUnit.group().equals(targetUnit.group())) {
            throw new IncompatibleUnitsException(
                "Incompatible units: " + sourceUnit.name() + " and " + targetUnit.name() + " cannot be converted");
        }

        BigDecimal result = request.value()
            .divide(sourceUnit.ratioToBaseUnit(), SCALE, RoundingMode.HALF_UP)
            .multiply(targetUnit.ratioToBaseUnit())
            .setScale(SCALE, RoundingMode.HALF_UP);

        return new ConversionResult(request.id(), request.value(), sourceUnit.name(), targetUnit.name(), result);
    }

    @Override
    public List<UnitResponse> units() {
        return unitCatalog.allUnits().stream()
            .map(unit -> new UnitResponse(UnitCatalogImpl.unitId(unit.name()), unit.name(), unit.system()))
            .toList();
    }

    private UnitDefinition unit(String unitName) {
        if (unitName == null) {
            throw new InvalidInputException("Invalid input: unit name is required");
        }

        return unitCatalog.findByName(normalize(unitName))
            .orElseThrow(() -> new InvalidInputException("Invalid input: unsupported unit '" + normalize(unitName) + "'"));
    }

    private String normalize(String unitName) {
        return unitName.trim().toLowerCase(Locale.ROOT);
    }
}
