package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversionResultDao {

    private static final Logger log = LoggerFactory.getLogger(ConversionResultDao.class);

    private final ConversionResultRepository conversionResultRepository;

    public ConversionResultDao(ConversionResultRepository conversionResultRepository) {
        this.conversionResultRepository = conversionResultRepository;
    }

    public ConversionResult save(ConversionResult result) {
        log.info("Persisting conversion result for {} from {} to {}", result.getInputValue(), result.getSourceUnit(), result.getTargetUnit());
        return conversionResultRepository.save(result);
    }

    public Optional<ConversionResult> findById(UUID id) {
        return conversionResultRepository.findById(id);
    }
}
