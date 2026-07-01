package com.example.unitconv.application;

import java.util.List;

import com.example.unitconv.api.request.ConversionRequest;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.api.response.UnitResponse;

public interface ConversionService {

    ConversionResult convert(ConversionRequest request);

    List<UnitResponse> units();
}
