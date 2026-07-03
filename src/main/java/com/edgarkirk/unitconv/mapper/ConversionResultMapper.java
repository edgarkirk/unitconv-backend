package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;

public final class ConversionResultMapper {

    private ConversionResultMapper() {
    }

    public static ConversionResultResponse toResponse(ConversionResult conversionResult) {
        return new ConversionResultResponse(
                conversionResult.id(),
                conversionResult.inputValue(),
                conversionResult.sourceUnit(),
                conversionResult.targetUnit(),
                conversionResult.result());
    }
}
