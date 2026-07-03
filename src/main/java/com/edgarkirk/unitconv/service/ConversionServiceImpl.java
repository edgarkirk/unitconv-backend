package com.edgarkirk.unitconv.service;

import java.util.List;
import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConversionServiceImpl implements ConversionService {

    private final UnitRepository unitRepository;
    private final ConversionResultRepository conversionResultRepository;

    public ConversionServiceImpl(UnitRepository unitRepository, ConversionResultRepository conversionResultRepository) {
        this.unitRepository = unitRepository;
        this.conversionResultRepository = conversionResultRepository;
    }

    @Override
    public ConversionResultResponse convert(ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion service not implemented yet");
    }

    @Override
    public List<UnitResponse> listUnits() {
        throw new UnsupportedOperationException("Conversion service not implemented yet");
    }
}
