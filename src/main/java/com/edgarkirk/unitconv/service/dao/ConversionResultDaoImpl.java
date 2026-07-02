package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ConversionResultDaoImpl implements ConversionResultDao {

    private final ConversionResultRepository conversionResultRepository;

    public ConversionResultDaoImpl(ConversionResultRepository conversionResultRepository) {
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    public ConversionResult save(ConversionResult conversionResult) {
        return conversionResultRepository.save(conversionResult);
    }
}
