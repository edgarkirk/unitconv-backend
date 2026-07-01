package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.request.ConversionRequest;
import com.edgarkirk.unitconv.api.response.ConversionResult;
import com.edgarkirk.unitconv.application.ConversionService;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api")
class ConversionController {

    private final ConversionService conversionService;

    ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    public ConversionResult convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request);
    }
}
