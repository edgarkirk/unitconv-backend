package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversionServiceImpl implements ConversionService {

    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final List<String> UNIT_ORDER = List.of("metres", "feet", "kilometres", "miles", "litres", "gallons");
    private static final Map<String, ConversionRule> CONVERSION_RULES = Map.of(
            "metres", new ConversionRule(UnitGroup.LENGTH, "feet", new BigDecimal("3.28084"), true),
            "feet", new ConversionRule(UnitGroup.LENGTH, "metres", new BigDecimal("3.28084"), false),
            "kilometres", new ConversionRule(UnitGroup.LENGTH, "miles", new BigDecimal("0.621371"), true),
            "miles", new ConversionRule(UnitGroup.LENGTH, "kilometres", new BigDecimal("0.621371"), false),
            "litres", new ConversionRule(UnitGroup.VOLUME, "gallons", new BigDecimal("0.264172"), true),
            "gallons", new ConversionRule(UnitGroup.VOLUME, "litres", new BigDecimal("0.264172"), false)
    );

    private final UnitDao unitDao;
    private final ConversionResultDao conversionResultDao;

    public ConversionServiceImpl(UnitDao unitDao, ConversionResultDao conversionResultDao) {
        this.unitDao = unitDao;
        this.conversionResultDao = conversionResultDao;
    }

    @Override
    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        Unit sourceUnit = resolveUnit(request.sourceUnit(), "sourceUnit");
        Unit targetUnit = resolveUnit(request.targetUnit(), "targetUnit");
        ConversionRule sourceRule = getRule(sourceUnit.getName());
        ConversionRule targetRule = getRule(targetUnit.getName());

        if (sourceRule.group() != targetRule.group()) {
            throw new IncompatibleUnitsException("Incompatible units: " + sourceUnit.getName() + " and " + targetUnit.getName());
        }

        BigDecimal result = convertValue(request.value(), sourceUnit.getName(), targetUnit.getName(), sourceRule, targetRule);
        ConversionResult saved = conversionResultDao.save(new ConversionResult(null,
                request.value(),
                sourceUnit.getName(),
                targetUnit.getName(),
                result));
        return toResponse(saved);
    }

    @Override
    public List<UnitResponse> listSupportedUnits() {
        return unitDao.findAllSupportedUnits().stream()
                .sorted(Comparator.comparingInt(unit -> canonicalOrder(unit.getName())))
                .map(unit -> new UnitResponse(unit.getId(), unit.getName(), unit.getSystem()))
                .toList();
    }

    private Unit resolveUnit(String unitName, String field) {
        Optional<Unit> unit = unitDao.findByName(unitName);
        if (unit.isEmpty()) {
            throw new UnknownUnitException("Unknown unit: " + unitName, field);
        }
        return unit.get();
    }

    private ConversionRule getRule(String unitName) {
        ConversionRule rule = CONVERSION_RULES.get(unitName);
        if (rule == null) {
            throw new UnknownUnitException("Unknown unit: " + unitName);
        }
        return rule;
    }

    private BigDecimal convertValue(BigDecimal value, String sourceUnit, String targetUnit, ConversionRule sourceRule, ConversionRule targetRule) {
        if (sourceUnit.equals(targetUnit)) {
            return value.setScale(SCALE, ROUNDING_MODE);
        }
        if (!sourceRule.counterpartUnit().equals(targetUnit) || !targetRule.counterpartUnit().equals(sourceUnit)) {
            throw new IllegalArgumentException("Unsupported conversion pair: " + sourceUnit + " and " + targetUnit);
        }
        return sourceRule.multiplyWhenSource()
                ? value.multiply(sourceRule.factor()).setScale(SCALE, ROUNDING_MODE)
                : value.divide(sourceRule.factor(), SCALE, ROUNDING_MODE);
    }

    private ConversionResultResponse toResponse(ConversionResult result) {
        return new ConversionResultResponse(result.getId(), result.getInputValue(), result.getSourceUnit(), result.getTargetUnit(), result.getResult());
    }

    private int canonicalOrder(String unitName) {
        int index = UNIT_ORDER.indexOf(unitName);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    private enum UnitGroup {
        LENGTH,
        VOLUME
    }

    private record ConversionRule(UnitGroup group, String counterpartUnit, BigDecimal factor, boolean multiplyWhenSource) {
    }
}
