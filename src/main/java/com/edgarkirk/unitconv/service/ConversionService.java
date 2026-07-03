package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConversionService {

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    public ConversionService(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Transactional
    public ConversionResultResponse convert(ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion service not implemented yet");
    }

    public List<UnitResponse> listUnits() {
        throw new UnsupportedOperationException("Unit listing not implemented yet");
    }
}
