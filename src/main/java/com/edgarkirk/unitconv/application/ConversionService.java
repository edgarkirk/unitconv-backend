package com.edgarkirk.unitconv.application;

import java.util.List;

import com.edgarkirk.unitconv.api.request.ConversionRequest;
import com.edgarkirk.unitconv.api.response.ConversionResult;
import com.edgarkirk.unitconv.api.response.Unit;

public interface ConversionService {

    ConversionResult convert(ConversionRequest request);

    List<Unit> listSupportedUnits();
}
