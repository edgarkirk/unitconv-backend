package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.621371");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");
    private static final int RESULT_SCALE = 12;

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        var sourceUnit = unitRepository.findByName(request.sourceUnit())
                .orElseThrow(() -> new ConversionValidationException(
                        "Source unit '" + request.sourceUnit() + "' is not supported.",
                        "sourceUnit"));
        var targetUnit = unitRepository.findByName(request.targetUnit())
                .orElseThrow(() -> new ConversionValidationException(
                        "Target unit '" + request.targetUnit() + "' is not supported.",
                        "targetUnit"));

        var result = convertValue(request.value(), sourceUnit.getName(), targetUnit.getName());
        var saved = conversionResultRepository.save(new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                null,
                request.value(),
                sourceUnit.getName(),
                targetUnit.getName(),
                result));
        return toResponse(saved);
    }

    @Override
    public List<Unit> getSupportedUnits() {
        return unitRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    private ConversionResult toResponse(com.edgarkirk.unitconv.persistence.entity.ConversionResult conversionResult) {
        return new ConversionResult(
                conversionResult.getId(),
                conversionResult.getInputValue(),
                conversionResult.getSourceUnit(),
                conversionResult.getTargetUnit(),
                conversionResult.getResult());
    }

    private Unit toResponse(com.edgarkirk.unitconv.persistence.entity.Unit unit) {
        return new Unit(unit.getId(), unit.getName(), unit.getSystem());
    }

    private BigDecimal convertValue(BigDecimal inputValue, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return inputValue;
        }
        return switch (sourceUnit + "->" + targetUnit) {
            case "metres->feet" -> inputValue.multiply(METRES_TO_FEET).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
            case "feet->metres" -> inputValue.divide(METRES_TO_FEET, RESULT_SCALE, RoundingMode.HALF_UP);
            case "kilometres->miles" -> inputValue.multiply(KILOMETRES_TO_MILES).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
            case "miles->kilometres" -> inputValue.divide(KILOMETRES_TO_MILES, RESULT_SCALE, RoundingMode.HALF_UP);
            case "litres->gallons" -> inputValue.multiply(LITRES_TO_GALLONS).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
            case "gallons->litres" -> inputValue.divide(LITRES_TO_GALLONS, RESULT_SCALE, RoundingMode.HALF_UP);
            default -> throw new ConversionValidationException(
                    "Units '" + sourceUnit + "' and '" + targetUnit + "' are incompatible and cannot be converted.",
                    null);
        };
    }
}
