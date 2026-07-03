package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.InvalidConversionException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionServiceImpl.class);
    private static final int RESULT_SCALE = 6;
    private static final String SOURCE_UNIT_FIELD = "sourceUnit";
    private static final String TARGET_UNIT_FIELD = "targetUnit";
    private static final Map<String, UnitGroup> UNIT_GROUPS = Map.of(
            "metres", UnitGroup.LENGTH,
            "feet", UnitGroup.LENGTH,
            "kilometres", UnitGroup.LENGTH,
            "miles", UnitGroup.LENGTH,
            "litres", UnitGroup.VOLUME,
            "gallons", UnitGroup.VOLUME
    );
    private static final Map<String, Map<String, ConversionRule>> CONVERSION_RULES = Map.of(
            "metres", Map.of("feet", new ConversionRule(new BigDecimal("3.280839895013123"), false)),
            "feet", Map.of("metres", new ConversionRule(new BigDecimal("3.280839895013123"), true)),
            "kilometres", Map.of("miles", new ConversionRule(new BigDecimal("0.621371192237334"), false)),
            "miles", Map.of("kilometres", new ConversionRule(new BigDecimal("0.621371192237334"), true)),
            "litres", Map.of("gallons", new ConversionRule(new BigDecimal("0.2641720523581484"), false)),
            "gallons", Map.of("litres", new ConversionRule(new BigDecimal("0.2641720523581484"), true))
    );

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        UnitEntity targetUnit = resolveUnit(request.targetUnit(), TARGET_UNIT_FIELD);
        UnitEntity sourceUnit = resolveUnit(request.sourceUnit(), SOURCE_UNIT_FIELD);
        validateConversion(sourceUnit.getName(), targetUnit.getName());

        ConversionRule rule = resolveRule(sourceUnit.getName(), targetUnit.getName());
        BigDecimal result = rule.apply(request.value());
        ConversionResultEntity savedResult = conversionResultRepository.save(
                new ConversionResultEntity(null, request.value(), sourceUnit.getName(), targetUnit.getName(), result)
        );
        log.info("Converted {} from {} to {} as {}", request.value(), sourceUnit.getName(), targetUnit.getName(), result);
        return new ConversionResultResponse(
                savedResult.getId(),
                savedResult.getInputValue(),
                savedResult.getSourceUnit(),
                savedResult.getTargetUnit(),
                savedResult.getResult()
        );
    }

    @Override
    public List<UnitResponse> listUnits() {
        return unitRepository.findAllByOrderByNameAsc().stream()
                .map(unit -> new UnitResponse(unit.getId(), unit.getName(), unit.getSystem()))
                .toList();
    }

    private UnitEntity resolveUnit(String unitName, String field) {
        return unitRepository.findByName(unitName)
                .orElseThrow(() -> new UnsupportedUnitException("Unsupported unit: " + unitName, field));
    }

    private void validateConversion(String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            throw new InvalidConversionException("Invalid conversion pair: " + sourceUnit + " and " + targetUnit, SOURCE_UNIT_FIELD);
        }

        UnitGroup sourceGroup = UNIT_GROUPS.get(sourceUnit);
        UnitGroup targetGroup = UNIT_GROUPS.get(targetUnit);
        if (sourceGroup != targetGroup) {
            throw new IncompatibleUnitsException("Incompatible units: " + sourceUnit + " and " + targetUnit, SOURCE_UNIT_FIELD);
        }

        if (resolveRule(sourceUnit, targetUnit) == null) {
            throw new IncompatibleUnitsException("Incompatible units: " + sourceUnit + " and " + targetUnit, SOURCE_UNIT_FIELD);
        }
    }

    private ConversionRule resolveRule(String sourceUnit, String targetUnit) {
        Map<String, ConversionRule> targetRules = CONVERSION_RULES.get(sourceUnit);
        if (targetRules == null) {
            return null;
        }
        return targetRules.get(targetUnit);
    }

    private enum UnitGroup {
        LENGTH,
        VOLUME
    }

    private record ConversionRule(BigDecimal factor, boolean inverse) {

        private BigDecimal apply(BigDecimal inputValue) {
            BigDecimal converted = inverse ? inputValue.divide(factor, RESULT_SCALE, RoundingMode.HALF_UP)
                    : inputValue.multiply(factor);
            BigDecimal normalized = converted.setScale(RESULT_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
            return normalized.scale() < 0 ? normalized.setScale(0, RoundingMode.HALF_UP) : normalized;
        }
    }
}
