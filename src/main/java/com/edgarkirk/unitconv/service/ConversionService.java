package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConversionService {

    private static final int RESULT_SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final List<String> UNIT_ORDER = List.of("metres", "feet", "kilometres", "miles", "litres", "gallons");
    private static final BigDecimal METRES_TO_FEET_FACTOR = new BigDecimal("0.3048");
    private static final BigDecimal KILOMETRES_TO_MILES_FACTOR = new BigDecimal("1.609344");
    private static final BigDecimal LITRES_TO_GALLONS_FACTOR = new BigDecimal("3.785411784");

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    public ConversionService(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        Unit sourceUnit = unitRepository.findByName(request.sourceUnit())
                .orElse(null);
        Unit targetUnit = unitRepository.findByName(request.targetUnit())
                .orElse(null);

        if (sourceUnit == null) {
            throw new UnsupportedUnitException("Unsupported unit: " + request.sourceUnit());
        }
        if (targetUnit == null) {
            throw new UnsupportedUnitException("Unsupported unit: " + request.targetUnit());
        }

        BigDecimal result = convertValue(request.value(), sourceUnit.getName(), targetUnit.getName());
        ConversionResult conversionResult = new ConversionResult(
                request.value(),
                sourceUnit.getName(),
                targetUnit.getName(),
                result);
        ConversionResult saved = conversionResultRepository.save(conversionResult);
        ConversionResult responseSource = saved != null ? saved : new ConversionResult(
                java.util.UUID.randomUUID(),
                conversionResult.getInputValue(),
                conversionResult.getSourceUnit(),
                conversionResult.getTargetUnit(),
                conversionResult.getResult());
        return new ConversionResultResponse(responseSource.getId(), responseSource.getInputValue(), responseSource.getSourceUnit(), responseSource.getTargetUnit(), responseSource.getResult());
    }

    public List<UnitResponse> listUnits() {
        return unitRepository.findAll().stream()
                .sorted(Comparator.comparingInt(unit -> unitOrder(unit.getName())))
                .map(unit -> new UnitResponse(unit.getId(), unit.getName(), unit.getSystem()))
                .toList();
    }

    private Unit requireUnit(String unitName) {
        return unitRepository.findByName(unitName)
                .orElseThrow(() -> new UnsupportedUnitException("Unsupported unit: " + unitName));
    }

    private BigDecimal convertValue(BigDecimal inputValue, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            throw incompatibleUnits(sourceUnit, targetUnit);
        }

        return switch (sourceUnit + "->" + targetUnit) {
            case "metres->feet" -> inputValue.divide(METRES_TO_FEET_FACTOR, RESULT_SCALE, ROUNDING_MODE);
            case "feet->metres" -> scaleResult(inputValue.multiply(METRES_TO_FEET_FACTOR));
            case "kilometres->miles" -> inputValue.divide(KILOMETRES_TO_MILES_FACTOR, RESULT_SCALE, ROUNDING_MODE);
            case "miles->kilometres" -> scaleResult(inputValue.multiply(KILOMETRES_TO_MILES_FACTOR));
            case "litres->gallons" -> inputValue.divide(LITRES_TO_GALLONS_FACTOR, RESULT_SCALE, ROUNDING_MODE);
            case "gallons->litres" -> scaleResult(inputValue.multiply(LITRES_TO_GALLONS_FACTOR));
            default -> throw incompatibleUnits(sourceUnit, targetUnit);
        };
    }

    private BigDecimal scaleResult(BigDecimal value) {
        return value.setScale(RESULT_SCALE, ROUNDING_MODE);
    }

    private IncompatibleUnitException incompatibleUnits(String sourceUnit, String targetUnit) {
        return new IncompatibleUnitException("Incompatible units: " + sourceUnit + " to " + targetUnit);
    }

    private int unitOrder(String unitName) {
        int index = UNIT_ORDER.indexOf(unitName);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }
}
