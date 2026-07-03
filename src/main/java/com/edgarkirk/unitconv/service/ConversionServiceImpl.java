package com.edgarkirk.unitconv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversionServiceImpl implements ConversionService {

    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    public ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        String sourceUnit = resolveCanonicalUnit(normalizeUnit(request.sourceUnit()), "sourceUnit");
        String targetUnit = resolveCanonicalUnit(normalizeUnit(request.targetUnit()), "targetUnit");

        BigDecimal result = convertValue(request.value(), sourceUnit, targetUnit);
        com.edgarkirk.unitconv.persistence.entity.ConversionResult savedResult = conversionResultRepository.save(
                new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                        request.value(),
                        sourceUnit,
                        targetUnit,
                        result));

        return new ConversionResult(
                savedResult.getId(),
                savedResult.getInputValue(),
                savedResult.getSourceUnit(),
                savedResult.getTargetUnit(),
                savedResult.getResult());
    }

    private BigDecimal convertValue(BigDecimal inputValue, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return inputValue.setScale(SCALE, ROUNDING_MODE);
        }

        if (sourceUnit.equals("metres") && targetUnit.equals("feet")) {
            return inputValue.multiply(METRES_TO_FEET).setScale(SCALE, ROUNDING_MODE);
        }
        if (sourceUnit.equals("feet") && targetUnit.equals("metres")) {
            return inputValue.divide(METRES_TO_FEET, SCALE, ROUNDING_MODE);
        }
        if (sourceUnit.equals("kilometres") && targetUnit.equals("miles")) {
            return inputValue.multiply(KILOMETRES_TO_MILES).setScale(SCALE, ROUNDING_MODE);
        }
        if (sourceUnit.equals("miles") && targetUnit.equals("kilometres")) {
            return inputValue.divide(KILOMETRES_TO_MILES, SCALE, ROUNDING_MODE);
        }
        if (sourceUnit.equals("litres") && targetUnit.equals("gallons")) {
            return inputValue.multiply(LITRES_TO_GALLONS).setScale(SCALE, ROUNDING_MODE);
        }
        if (sourceUnit.equals("gallons") && targetUnit.equals("litres")) {
            return inputValue.divide(LITRES_TO_GALLONS, SCALE, ROUNDING_MODE);
        }

        throw new IncompatibleUnitsException("incompatible units: " + sourceUnit + " cannot be converted to " + targetUnit);
    }

    private String resolveCanonicalUnit(String unitName, String fieldName) {
        return unitRepository.findByNameIgnoreCase(unitName)
                .map(com.edgarkirk.unitconv.persistence.entity.Unit::getName)
                .orElseThrow(() -> new UnknownUnitException("unknown " + fieldName + ": " + unitName, fieldName));
    }

    private String normalizeUnit(String unitName) {
        return unitName.trim().toLowerCase(Locale.ROOT);
    }
}
