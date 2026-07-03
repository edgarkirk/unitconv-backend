package com.edgarkirk.unitconv.mapper;

import java.math.BigDecimal;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;

public final class ConversionResultMapper {

    private ConversionResultMapper() {
    }

    public static ConversionResult toResponse(com.edgarkirk.unitconv.persistence.entity.ConversionResult entity) {
        return new ConversionResult(
                entity.getId(),
                entity.getInputValue(),
                entity.getSourceUnit(),
                entity.getTargetUnit(),
                entity.getResult());
    }

    public static com.edgarkirk.unitconv.persistence.entity.ConversionResult toEntity(
            ConversionRequest request,
            String sourceUnit,
            String targetUnit,
            BigDecimal result) {
        return new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                request.value(),
                sourceUnit,
                targetUnit,
                result);
    }
}
