package com.edgarkirk.unitconv.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.service.ConversionService;

import jakarta.validation.Valid;

@RestController
@Validated
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/api/convert")
    public ConversionResultResponse convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request);
    }
}
