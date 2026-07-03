package com.edgarkirk.unitconv.service;

import java.util.List;
import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;

public interface ConversionService {

    ConversionResultResponse convert(ConversionRequest request);

    List<UnitResponse> listUnits();
}
