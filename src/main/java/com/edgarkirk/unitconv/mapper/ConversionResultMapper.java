package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import java.math.BigDecimal;

public final class ConversionResultMapper {

    private ConversionResultMapper() {
    }

    public static ConversionResult toEntity(ConversionRequest request, BigDecimal result) {
        ConversionResult conversionResult = ConversionResult.create();
        conversionResult.setInputValue(request.value());
        conversionResult.setSourceUnit(request.sourceUnit());
        conversionResult.setTargetUnit(request.targetUnit());
        conversionResult.setResult(result);
        return conversionResult;
    }

    public static ConversionResultResponse toResponse(ConversionResult conversionResult) {
        return new ConversionResultResponse(
                conversionResult.getId(),
                conversionResult.getInputValue(),
                conversionResult.getSourceUnit(),
                conversionResult.getTargetUnit(),
                conversionResult.getResult());
    }
}
