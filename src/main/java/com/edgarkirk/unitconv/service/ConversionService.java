package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.mapper.ConversionResultMapper;
import com.edgarkirk.unitconv.mapper.UnitMapper;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversionService {

    private static final int SCALE = 6;
    private static final MathContext CALCULATION_CONTEXT = new MathContext(20, RoundingMode.HALF_UP);
    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");
    private static final BigDecimal FEET_TO_METRES = BigDecimal.ONE.divide(METRES_TO_FEET, CALCULATION_CONTEXT);
    private static final BigDecimal MILES_TO_METRES = new BigDecimal("1000").divide(KILOMETRES_TO_MILES, CALCULATION_CONTEXT);
    private static final BigDecimal GALLONS_TO_LITRES = BigDecimal.ONE.divide(LITRES_TO_GALLONS, CALCULATION_CONTEXT);
    private static final Map<String, BigDecimal> TO_BASE_FACTORS = Map.of(
            "metres", BigDecimal.ONE,
            "feet", FEET_TO_METRES,
            "kilometres", new BigDecimal("1000"),
            "miles", MILES_TO_METRES,
            "litres", BigDecimal.ONE,
            "gallons", GALLONS_TO_LITRES);
    private static final Map<String, String> UNIT_GROUPS = Map.of(
            "metres", "length",
            "feet", "length",
            "kilometres", "length",
            "miles", "length",
            "litres", "volume",
            "gallons", "volume");
    private static final Set<String> SUPPORTED_UNITS = TO_BASE_FACTORS.keySet();

    private final UnitDao unitDao;
    private final ConversionResultDao conversionResultDao;

    public ConversionService(UnitDao unitDao, ConversionResultDao conversionResultDao) {
        this.unitDao = unitDao;
        this.conversionResultDao = conversionResultDao;
    }

    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        Unit sourceUnit = resolveUnit(request.sourceUnit(), "sourceUnit");
        Unit targetUnit = resolveUnit(request.targetUnit(), "targetUnit");

        if (!areCompatible(sourceUnit.getName(), targetUnit.getName())) {
            throw new IncompatibleUnitsException(sourceUnit.getName(), targetUnit.getName());
        }

        BigDecimal result = convertValue(request.value(), sourceUnit.getName(), targetUnit.getName());
        ConversionResult saved = conversionResultDao.save(ConversionResultMapper.toEntity(request, result));
        return ConversionResultMapper.toResponse(saved);
    }

    public List<UnitResponse> getUnits() {
        return unitDao.findAllByOrderByNameAsc().stream()
                .map(UnitMapper::toResponse)
                .toList();
    }

    private Unit resolveUnit(String unitName, String field) {
        return unitDao.findByName(unitName)
                .orElseThrow(() -> new UnsupportedUnitException(unitName, field));
    }

    private boolean areCompatible(String sourceUnit, String targetUnit) {
        return UNIT_GROUPS.get(sourceUnit).equals(UNIT_GROUPS.get(targetUnit));
    }

    private BigDecimal convertValue(BigDecimal value, String sourceUnit, String targetUnit) {
        BigDecimal sourceFactor = toBaseFactor(sourceUnit);
        BigDecimal targetFactor = toBaseFactor(targetUnit);
        return value.multiply(sourceFactor, CALCULATION_CONTEXT)
                .divide(targetFactor, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal toBaseFactor(String unitName) {
        BigDecimal factor = TO_BASE_FACTORS.get(unitName);
        if (factor == null || !SUPPORTED_UNITS.contains(unitName)) {
            throw new UnsupportedUnitException(unitName, null);
        }
        return factor;
    }
}
