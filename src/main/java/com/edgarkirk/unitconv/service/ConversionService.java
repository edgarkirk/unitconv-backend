package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import java.util.List;

public interface ConversionService {

    ConversionResultResponse convert(ConversionRequest request);

    List<UnitResponse> listSupportedUnits();
}
