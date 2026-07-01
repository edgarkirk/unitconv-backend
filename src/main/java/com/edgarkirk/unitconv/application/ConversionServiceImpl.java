package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
class ConversionServiceImpl implements ConversionService {

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    @Transactional
    public ConversionResult convert(ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion service is not implemented yet");
    }

    @Override
    public List<Unit> getSupportedUnits() {
        throw new UnsupportedOperationException("Unit listing is not implemented yet");
    }
}
