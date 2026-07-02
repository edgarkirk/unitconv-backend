package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;

public class ConversionResultMapper {

    public ConversionResultResponse toResponse(ConversionResult conversionResult) {
        return new ConversionResultResponse(
                conversionResult.getId(),
                conversionResult.getInputValue(),
                conversionResult.getSourceUnit(),
                conversionResult.getTargetUnit(),
                conversionResult.getResult());
    }
}
