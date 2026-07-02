package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.mapper.ConversionResultMapper;
import com.edgarkirk.unitconv.mapper.UnitMapper;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.InvalidConversionException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionServiceImpl.class);
    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");
    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final UnitDao unitDao;
    private final ConversionResultDao conversionResultDao;

    ConversionServiceImpl(UnitDao unitDao, ConversionResultDao conversionResultDao) {
        this.unitDao = unitDao;
        this.conversionResultDao = conversionResultDao;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        validateRequest(request);
        Map<String, com.edgarkirk.unitconv.persistence.entity.Unit> unitsByName = loadUnitsByName();

        com.edgarkirk.unitconv.persistence.entity.Unit sourceUnit = resolveUnit(request.sourceUnit(), "sourceUnit", unitsByName);
        com.edgarkirk.unitconv.persistence.entity.Unit targetUnit = resolveUnit(request.targetUnit(), "targetUnit", unitsByName);

        BigDecimal result = convertValue(request.value(), sourceUnit.getName(), targetUnit.getName());
        com.edgarkirk.unitconv.persistence.entity.ConversionResult saved = conversionResultDao.save(
                new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                        scale(request.value()),
                        sourceUnit.getName(),
                        targetUnit.getName(),
                        result));

        log.info("Converted {} {} to {} {}", request.value(), sourceUnit.getName(), result, targetUnit.getName());
        return ConversionResultMapper.toResponse(saved);
    }

    @Override
    public List<Unit> listUnits() {
        return unitDao.findAll().stream()
                .sorted((first, second) -> first.getName().compareTo(second.getName()))
                .map(UnitMapper::toResponse)
                .toList();
    }

    private Map<String, com.edgarkirk.unitconv.persistence.entity.Unit> loadUnitsByName() {
        List<com.edgarkirk.unitconv.persistence.entity.Unit> units = unitDao.findAll();
        if (units.isEmpty()) {
            return Map.of();
        }
        return units.stream().collect(Collectors.toMap(com.edgarkirk.unitconv.persistence.entity.Unit::getName, Function.identity(), (left, right) -> left, LinkedHashMap::new));
    }

    private com.edgarkirk.unitconv.persistence.entity.Unit resolveUnit(String unitName, String fieldName,
            Map<String, com.edgarkirk.unitconv.persistence.entity.Unit> unitsByName) {
        com.edgarkirk.unitconv.persistence.entity.Unit unit = unitsByName.get(unitName);
        if (unit != null) {
            return unit;
        }
        return unitDao.findByName(unitName)
                .orElseThrow(() -> new UnsupportedUnitException(unitName, fieldName));
    }

    private BigDecimal convertValue(BigDecimal value, String sourceUnit, String targetUnit) {
        if (Objects.equals(sourceUnit, targetUnit)) {
            return scale(value);
        }
        if ("metres".equals(sourceUnit) && "feet".equals(targetUnit)) {
            return scale(value.multiply(METRES_TO_FEET));
        }
        if ("feet".equals(sourceUnit) && "metres".equals(targetUnit)) {
            return scale(value.divide(METRES_TO_FEET, SCALE, ROUNDING_MODE));
        }
        if ("kilometres".equals(sourceUnit) && "miles".equals(targetUnit)) {
            return scale(value.multiply(KILOMETRES_TO_MILES));
        }
        if ("miles".equals(sourceUnit) && "kilometres".equals(targetUnit)) {
            return scale(value.divide(KILOMETRES_TO_MILES, SCALE, ROUNDING_MODE));
        }
        if ("litres".equals(sourceUnit) && "gallons".equals(targetUnit)) {
            return scale(value.multiply(LITRES_TO_GALLONS));
        }
        if ("gallons".equals(sourceUnit) && "litres".equals(targetUnit)) {
            return scale(value.divide(LITRES_TO_GALLONS, SCALE, ROUNDING_MODE));
        }
        throw new IncompatibleUnitsException(sourceUnit, targetUnit);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING_MODE);
    }

    private void validateRequest(ConversionRequest request) {
        if (request == null) {
            throw new InvalidConversionException("Missing conversion request body");
        }
        if (request.value() == null) {
            throw new InvalidConversionException("Missing required field: value", "value");
        }
        if (request.sourceUnit() == null || request.sourceUnit().isBlank()) {
            throw new InvalidConversionException("Missing required field: sourceUnit", "sourceUnit");
        }
        if (request.targetUnit() == null || request.targetUnit().isBlank()) {
            throw new InvalidConversionException("Missing required field: targetUnit", "targetUnit");
        }
    }
}
