package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    private final UnitDao unitDao;
    private final ConversionResultDao conversionResultDao;

    public ConversionService(UnitDao unitDao, ConversionResultDao conversionResultDao) {
        this.unitDao = unitDao;
        this.conversionResultDao = conversionResultDao;
    }

    public ConversionResultResponse convert(ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion service is not implemented yet");
    }

    public List<UnitResponse> getUnits() {
        throw new UnsupportedOperationException("Unit lookup is not implemented yet");
    }
}
