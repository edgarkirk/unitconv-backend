package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.mapper.ConversionResultMapper;
import com.edgarkirk.unitconv.mapper.UnitMapper;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ConversionServiceImpl implements ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionServiceImpl.class);
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final BigDecimal METRES_PER_FOOT = new BigDecimal("0.3048");
    private static final BigDecimal KILOMETRES_PER_MILE = new BigDecimal("1.609344");
    private static final BigDecimal LITRES_PER_GALLON = new BigDecimal("3.785411784");

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;
    private final ConversionResultMapper conversionResultMapper;
    private final UnitMapper unitMapper;

    ConversionServiceImpl(UnitRepository unitRepository,
                          ConversionResultRepository conversionResultRepository,
                          ConversionResultMapper conversionResultMapper,
                          UnitMapper unitMapper) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
        this.conversionResultMapper = conversionResultMapper;
        this.unitMapper = unitMapper;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        com.edgarkirk.unitconv.persistence.entity.Unit source = resolveUnit(request.sourceUnit(), "sourceUnit");
        com.edgarkirk.unitconv.persistence.entity.Unit target = resolveUnit(request.targetUnit(), "targetUnit");
        validateCompatibility(source, target);

        BigDecimal result = convertValue(request.value(), source.getName(), target.getName());
        com.edgarkirk.unitconv.persistence.entity.ConversionResult saved = conversionResultRepository.save(
                new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                        null,
                        request.value(),
                        source.getName(),
                        target.getName(),
                        result));

        log.info("Converted {} from {} to {}", request.value(), source.getName(), target.getName());
        return conversionResultMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> getSupportedUnits() {
        return unitMapper.toResponses(unitRepository.findAll());
    }

    private com.edgarkirk.unitconv.persistence.entity.Unit resolveUnit(String unitName, String field) {
        return unitRepository.findByName(unitName)
                .orElseThrow(() -> new UnknownUnitException(field, "Unknown " + field + ": " + unitName));
    }

    private void validateCompatibility(com.edgarkirk.unitconv.persistence.entity.Unit source,
                                       com.edgarkirk.unitconv.persistence.entity.Unit target) {
        String sourceName = source.getName();
        String targetName = target.getName();
        if (sourceName.equals(targetName)) {
            return;
        }

        boolean supportedPair = (sourceName.equals("metres") && targetName.equals("feet"))
                || (sourceName.equals("feet") && targetName.equals("metres"))
                || (sourceName.equals("kilometres") && targetName.equals("miles"))
                || (sourceName.equals("miles") && targetName.equals("kilometres"))
                || (sourceName.equals("litres") && targetName.equals("gallons"))
                || (sourceName.equals("gallons") && targetName.equals("litres"));
        if (!supportedPair) {
            throw new IncompatibleUnitException("Incompatible units: " + sourceName + " and " + targetName);
        }
    }

    private BigDecimal convertValue(BigDecimal inputValue, String sourceUnit, String targetUnit) {
        if (sourceUnit.equals(targetUnit)) {
            return inputValue;
        }

        return switch (sourceUnit) {
            case "metres" -> switch (targetUnit) {
                case "feet" -> inputValue.divide(METRES_PER_FOOT, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            case "feet" -> switch (targetUnit) {
                case "metres" -> inputValue.multiply(METRES_PER_FOOT, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            case "kilometres" -> switch (targetUnit) {
                case "miles" -> inputValue.divide(KILOMETRES_PER_MILE, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            case "miles" -> switch (targetUnit) {
                case "kilometres" -> inputValue.multiply(KILOMETRES_PER_MILE, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            case "litres" -> switch (targetUnit) {
                case "gallons" -> inputValue.divide(LITRES_PER_GALLON, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            case "gallons" -> switch (targetUnit) {
                case "litres" -> inputValue.multiply(LITRES_PER_GALLON, MATH_CONTEXT);
                default -> throw new IncompatibleUnitException("Incompatible units: " + sourceUnit + " and " + targetUnit);
            };
            default -> throw new UnknownUnitException("sourceUnit", "Unknown sourceUnit: " + sourceUnit);
        };
    }
}
