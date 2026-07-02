package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversionResultDao {

    private final ConversionResultRepository conversionResultRepository;

    public ConversionResultDao(ConversionResultRepository conversionResultRepository) {
        this.conversionResultRepository = conversionResultRepository;
    }

    public ConversionResult save(ConversionResult result) {
        throw new UnsupportedOperationException("ConversionResultDao.save is not implemented yet");
    }

    public Optional<ConversionResult> findById(UUID id) {
        throw new UnsupportedOperationException("ConversionResultDao.findById is not implemented yet");
    }
}
