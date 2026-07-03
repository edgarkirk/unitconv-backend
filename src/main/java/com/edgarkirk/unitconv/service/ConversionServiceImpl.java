package com.edgarkirk.unitconv.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.mapper.ConversionResultMapper;
import com.edgarkirk.unitconv.mapper.UnitMapper;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitPairException;
import com.edgarkirk.unitconv.service.exception.UnitNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversionServiceImpl implements ConversionService {

    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.280839895013123");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.62137119223733");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.2641720523581484");
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    public ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        Unit sourceUnit = findUnit(request.sourceUnit(), "sourceUnit", "source unit");
        Unit targetUnit = findUnit(request.targetUnit(), "targetUnit", "target unit");

        if (!sameGroup(sourceUnit, targetUnit)) {
            throw new IncompatibleUnitPairException("Units " + sourceUnit.name() + " and " + targetUnit.name() + " are incompatible");
        }

        BigDecimal convertedValue = convertValue(request.value(), sourceUnit.name(), targetUnit.name());
        ConversionResult saved = conversionResultRepository.save(new ConversionResult(
                UUID.randomUUID(),
                request.value(),
                sourceUnit.name(),
                targetUnit.name(),
                convertedValue));

        return ConversionResultMapper.toResponse(saved);
    }

    @Override
    public List<UnitResponse> listSupportedUnits() {
        return unitRepository.findAllByOrderByNameAsc().stream()
                .map(UnitMapper::toResponse)
                .toList();
    }

    private Unit findUnit(String name, String field, String label) {
        return unitRepository.findByName(name)
                .orElseThrow(() -> new UnitNotFoundException("Unknown " + label + ": " + name, field));
    }

    private boolean sameGroup(Unit sourceUnit, Unit targetUnit) {
        return groupOf(sourceUnit.name()) == groupOf(targetUnit.name());
    }

    private UnitGroup groupOf(String unitName) {
        String canonicalName = unitName.toLowerCase(Locale.ROOT);
        return switch (canonicalName) {
            case "metres", "feet", "kilometres", "miles" -> UnitGroup.LENGTH;
            case "litres", "gallons" -> UnitGroup.VOLUME;
            default -> throw new UnitNotFoundException("Unknown unit: " + unitName);
        };
    }

    private BigDecimal convertValue(BigDecimal value, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return value;
        }

        if (sourceUnit.equals("metres") && targetUnit.equals("feet")) {
            return value.multiply(METRES_TO_FEET, MATH_CONTEXT);
        }
        if (sourceUnit.equals("feet") && targetUnit.equals("metres")) {
            return value.divide(METRES_TO_FEET, MATH_CONTEXT);
        }
        if (sourceUnit.equals("kilometres") && targetUnit.equals("miles")) {
            return value.multiply(KILOMETRES_TO_MILES, MATH_CONTEXT);
        }
        if (sourceUnit.equals("miles") && targetUnit.equals("kilometres")) {
            return value.divide(KILOMETRES_TO_MILES, MATH_CONTEXT);
        }
        if (sourceUnit.equals("litres") && targetUnit.equals("gallons")) {
            return value.multiply(LITRES_TO_GALLONS, MATH_CONTEXT);
        }
        if (sourceUnit.equals("gallons") && targetUnit.equals("litres")) {
            return value.divide(LITRES_TO_GALLONS, MATH_CONTEXT);
        }
        throw new IncompatibleUnitPairException("Units " + sourceUnit + " and " + targetUnit + " are incompatible");
    }

    private enum UnitGroup {
        LENGTH,
        VOLUME
    }
}
