package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;

import java.util.List;

public interface ConversionService {

    ConversionResult convert(ConversionRequest request);

    List<Unit> getSupportedUnits();
}
