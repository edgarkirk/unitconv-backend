package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import org.springframework.stereotype.Component;

@Component
public class ConversionResultMapper {

    public ConversionResult toResponse(com.edgarkirk.unitconv.persistence.entity.ConversionResult entity) {
        return new ConversionResult(
                entity.getId(),
                entity.getInputValue(),
                entity.getSourceUnit(),
                entity.getTargetUnit(),
                entity.getResult());
    }
}
