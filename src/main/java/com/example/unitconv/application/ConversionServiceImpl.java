package com.example.unitconv.application;

import java.util.List;

import com.example.unitconv.api.request.ConversionRequest;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.api.response.UnitResponse;
import org.springframework.stereotype.Service;

@Service
class ConversionServiceImpl implements ConversionService {

    private final UnitCatalog unitCatalog;

    ConversionServiceImpl(UnitCatalog unitCatalog) {
        this.unitCatalog = unitCatalog;
    }

    @Override
    public ConversionResult convert(ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion service not implemented yet");
    }

    @Override
    public List<UnitResponse> units() {
        throw new UnsupportedOperationException("Unit listing not implemented yet");
    }
}
