package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;

public interface ConversionService {

    ConversionResult convert(ConversionRequest request);
}
