package com.edgarkirk.unitconv;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static UnitEntity unit(String name, String system) {
        return new UnitEntity(UUID.randomUUID(), name, system);
    }

    public static UnitResponse unitResponse(String name, String system) {
        return new UnitResponse(UUID.randomUUID(), name, system);
    }

    public static ConversionResultEntity conversionResult(BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
        return new ConversionResultEntity(UUID.randomUUID(), inputValue, sourceUnit, targetUnit, result);
    }

    public static ConversionResultResponse conversionResultResponse(BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
        return new ConversionResultResponse(UUID.randomUUID(), inputValue, sourceUnit, targetUnit, result);
    }

    public static List<UnitResponse> supportedUnits() {
        return List.of(
                unitResponse("feet", "imperial"),
                unitResponse("gallons", "imperial"),
                unitResponse("kilometres", "metric"),
                unitResponse("litres", "metric"),
                unitResponse("metres", "metric"),
                unitResponse("miles", "imperial"));
    }
}
