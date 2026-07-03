package com.edgarkirk.unitconv.service.impl;

import org.springframework.stereotype.Service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.ConversionService;

@Service
public class ConversionServiceImpl implements ConversionService {

    private final ConversionResultRepository conversionResultRepository;
    private final UnitRepository unitRepository;

    public ConversionServiceImpl(ConversionResultRepository conversionResultRepository, UnitRepository unitRepository) {
        this.conversionResultRepository = conversionResultRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public ConversionResultResponse convert(ConversionRequest request) {
        return null;
    }
}
