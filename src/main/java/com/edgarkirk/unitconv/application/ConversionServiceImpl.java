package com.edgarkirk.unitconv.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import com.edgarkirk.unitconv.api.request.ConversionRequest;
import com.edgarkirk.unitconv.api.response.ConversionResult;
import com.edgarkirk.unitconv.api.response.Unit;
import com.edgarkirk.unitconv.application.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.application.exception.UnsupportedUnitException;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private static final int RESULT_SCALE = 6;
    private static final RoundingMode RESULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        String sourceUnit = normalize(request.sourceUnit());
        String targetUnit = normalize(request.targetUnit());
        resolveUnit(sourceUnit, "sourceUnit");
        resolveUnit(targetUnit, "targetUnit");
        BigDecimal result = convertValue(sourceUnit, targetUnit, request.value());
        ConversionResultEntity entity = new ConversionResultEntity(request.value(), sourceUnit, targetUnit, result);
        ConversionResultEntity saved = conversionResultRepository.saveAndFlush(entity);
        return new ConversionResult(saved.getId(), request.value(), sourceUnit, targetUnit, result);
    }

    @Override
    public List<Unit> listSupportedUnits() {
        return unitRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponseUnit)
                .toList();
    }

    private UnitEntity resolveUnit(String normalizedUnit, String field) {
        return unitRepository.findByNameIgnoreCase(normalizedUnit)
                .orElseThrow(() -> new UnsupportedUnitException(field, normalizedUnit));
    }

    private Unit toResponseUnit(UnitEntity entity) {
        return new Unit(entity.getId(), entity.getName(), entity.getSystem());
    }

    private BigDecimal convertValue(String sourceUnit, String targetUnit, BigDecimal value) {
        if (sourceUnit.equals(targetUnit)) {
            return scale(value);
        }

        return switch (sourceUnit) {
            case "metres" -> convertPair(sourceUnit, targetUnit, value, "metres", "feet", METRES_TO_FEET);
            case "feet" -> convertPair(sourceUnit, targetUnit, value, "metres", "feet", METRES_TO_FEET);
            case "kilometres" -> convertPair(sourceUnit, targetUnit, value, "kilometres", "miles", KILOMETRES_TO_MILES);
            case "miles" -> convertPair(sourceUnit, targetUnit, value, "kilometres", "miles", KILOMETRES_TO_MILES);
            case "litres" -> convertPair(sourceUnit, targetUnit, value, "litres", "gallons", LITRES_TO_GALLONS);
            case "gallons" -> convertPair(sourceUnit, targetUnit, value, "litres", "gallons", LITRES_TO_GALLONS);
            default -> throw new UnsupportedUnitException("sourceUnit", sourceUnit);
        };
    }

    private BigDecimal convertPair(String sourceUnit, String targetUnit, BigDecimal value, String forwardSourceUnit, String reverseSourceUnit, BigDecimal factor) {
        if (sourceUnit.equals(forwardSourceUnit) && targetUnit.equals(reverseSourceUnit)) {
            return scale(value.multiply(factor));
        }
        if (sourceUnit.equals(reverseSourceUnit) && targetUnit.equals(forwardSourceUnit)) {
            return scale(value.divide(factor, RESULT_SCALE, RESULT_ROUNDING_MODE));
        }
        throw new IncompatibleUnitsException(sourceUnit, targetUnit);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(RESULT_SCALE, RESULT_ROUNDING_MODE);
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
