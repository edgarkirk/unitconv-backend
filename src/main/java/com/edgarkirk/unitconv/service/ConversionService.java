package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.request.ConversionRequest;
import com.edgarkirk.unitconv.api.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.response.UnitResponse;
import java.util.List;

public interface ConversionService {

    ConversionResultResponse convert(ConversionRequest request);

    List<UnitResponse> listSupportedUnits();
}
