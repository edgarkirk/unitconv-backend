package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConversionServiceImpl implements ConversionService {

    private final UnitDao unitDao;
    private final ConversionResultDao conversionResultDao;

    public ConversionServiceImpl(UnitDao unitDao, ConversionResultDao conversionResultDao) {
        this.unitDao = unitDao;
        this.conversionResultDao = conversionResultDao;
    }

    @Override
    public ConversionResult convert(ConversionRequest request) {
        throw new UnsupportedOperationException("ConversionServiceImpl.convert is not implemented yet");
    }

    @Override
    public List<Unit> listUnits() {
        throw new UnsupportedOperationException("ConversionServiceImpl.listUnits is not implemented yet");
    }
}
