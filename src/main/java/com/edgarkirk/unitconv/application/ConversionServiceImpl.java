package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.application.exception.ConversionValidationException;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.UnitRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private static final int RESULT_SCALE = 6;
    private static final Map<String, BigDecimal> FACTORS = Map.of(
            pair("metres", "feet"), new BigDecimal("3.28084"),
            pair("feet", "metres"), new BigDecimal("0.3048"),
            pair("kilometres", "miles"), new BigDecimal("0.621371"),
            pair("miles", "kilometres"), new BigDecimal("1.60934"),
            pair("litres", "gallons"), new BigDecimal("0.264172"),
            pair("gallons", "litres"), new BigDecimal("3.78541"));

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    ConversionServiceImpl(final UnitRepository unitRepository, final ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResult convert(final ConversionRequest request) {
        validateUnitExists(request.sourceUnit(), "sourceUnit", "Source unit '%s' is not supported.");
        validateUnitExists(request.targetUnit(), "targetUnit", "Target unit '%s' is not supported.");
        validateCompatibility(request.sourceUnit(), request.targetUnit());

        final BigDecimal resultValue = calculateResult(request.value(), request.sourceUnit(), request.targetUnit());
        final com.edgarkirk.unitconv.persistence.ConversionResult persisted = conversionResultRepository.save(
                new com.edgarkirk.unitconv.persistence.ConversionResult(
                        request.value(),
                        request.sourceUnit(),
                        request.targetUnit(),
                        resultValue));

        return new ConversionResult(
                request.id(),
                persisted.getInputValue(),
                persisted.getSourceUnit(),
                persisted.getTargetUnit(),
                persisted.getResult());
    }

    @Override
    public List<Unit> listUnits() {
        return unitRepository.findAll().stream()
                .map(unit -> new Unit(unit.getId(), unit.getName(), unit.getSystem()))
                .toList();
    }

    private void validateUnitExists(final String unitName, final String field, final String messageTemplate) {
        unitRepository.findByName(unitName).orElseThrow(() -> new ConversionValidationException(
                messageTemplate.formatted(unitName),
                field));
    }

    private void validateCompatibility(final String sourceUnit, final String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return;
        }
        if (!FACTORS.containsKey(pair(sourceUnit, targetUnit))) {
            throw new ConversionValidationException(
                    "Units '%s' and '%s' are incompatible.".formatted(sourceUnit, targetUnit),
                    "sourceUnit,targetUnit");
        }
    }

    private BigDecimal calculateResult(final BigDecimal value, final String sourceUnit, final String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return value;
        }
        return value.multiply(FACTORS.get(pair(sourceUnit, targetUnit))).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private static String pair(final String sourceUnit, final String targetUnit) {
        return sourceUnit + "->" + targetUnit;
    }
}
