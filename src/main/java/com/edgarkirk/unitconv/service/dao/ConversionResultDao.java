package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.springframework.stereotype.Component;

@Component
public class ConversionResultDao {

    private final ConversionResultRepository conversionResultRepository;

    public ConversionResultDao(ConversionResultRepository conversionResultRepository) {
        this.conversionResultRepository = conversionResultRepository;
    }

    public ConversionResult save(ConversionResult conversionResult) {
        return conversionResultRepository.save(conversionResult);
    }
}
