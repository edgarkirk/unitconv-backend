package com.edgarkirk.unitconv.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;

@Service
@Transactional(readOnly = true)
public class ConversionServiceImpl implements ConversionService {

    private static final int SCALE = 6;
    private static final BigDecimal METRES_TO_FEET = new BigDecimal("3.28084");
    private static final BigDecimal KILOMETRES_TO_MILES = new BigDecimal("0.6213714285714286");
    private static final BigDecimal LITRES_TO_GALLONS = new BigDecimal("0.264172");

    private final ConversionResultRepository conversionResultRepository;
    private final UnitRepository unitRepository;

    public ConversionServiceImpl(ConversionResultRepository conversionResultRepository, UnitRepository unitRepository) {
        this.conversionResultRepository = conversionResultRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        unitRepository.findByName(request.sourceUnit())
                .orElseThrow(() -> new UnsupportedUnitException("Unsupported unit: " + request.sourceUnit()));
        unitRepository.findByName(request.targetUnit())
                .orElseThrow(() -> new UnsupportedUnitException("Unsupported unit: " + request.targetUnit()));

        BigDecimal result = convertValue(request.value(), request.sourceUnit(), request.targetUnit());
        ConversionResultEntity saved = conversionResultRepository.save(
                new ConversionResultEntity(request.value(), request.sourceUnit(), request.targetUnit(), result));
        return new ConversionResultResponse(saved.getId(), saved.getInputValue(), saved.getSourceUnit(), saved.getTargetUnit(), saved.getResult());
    }

    private BigDecimal convertValue(BigDecimal value, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return value;
        }
        return switch (sourceUnit) {
            case "metres" -> convertMetres(value, targetUnit);
            case "feet" -> convertFeet(value, targetUnit);
            case "kilometres" -> convertKilometres(value, targetUnit);
            case "miles" -> convertMiles(value, targetUnit);
            case "litres" -> convertLitres(value, targetUnit);
            case "gallons" -> convertGallons(value, targetUnit);
            default -> throw new UnsupportedUnitException("Unsupported unit: " + sourceUnit);
        };
    }

    private BigDecimal convertMetres(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "feet" -> value.multiply(METRES_TO_FEET).setScale(SCALE, RoundingMode.HALF_UP);
            default -> incompatible("metres", targetUnit);
        };
    }

    private BigDecimal convertFeet(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "metres" -> value.divide(METRES_TO_FEET, SCALE, RoundingMode.HALF_UP);
            default -> incompatible("feet", targetUnit);
        };
    }

    private BigDecimal convertKilometres(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "miles" -> value.multiply(KILOMETRES_TO_MILES).setScale(SCALE, RoundingMode.HALF_UP);
            default -> incompatible("kilometres", targetUnit);
        };
    }

    private BigDecimal convertMiles(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "kilometres" -> value.divide(KILOMETRES_TO_MILES, SCALE, RoundingMode.HALF_UP);
            default -> incompatible("miles", targetUnit);
        };
    }

    private BigDecimal convertLitres(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "gallons" -> value.multiply(LITRES_TO_GALLONS).setScale(SCALE, RoundingMode.HALF_UP);
            default -> incompatible("litres", targetUnit);
        };
    }

    private BigDecimal convertGallons(BigDecimal value, String targetUnit) {
        return switch (targetUnit) {
            case "litres" -> value.divide(LITRES_TO_GALLONS, SCALE, RoundingMode.HALF_UP);
            default -> incompatible("gallons", targetUnit);
        };
    }

    private BigDecimal incompatible(String sourceUnit, String targetUnit) {
        throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " to " + targetUnit);
    }
}
