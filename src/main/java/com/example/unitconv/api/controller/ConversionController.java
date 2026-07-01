package com.example.unitconv.api.controller;

import com.example.unitconv.api.request.ConversionRequest;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.application.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Conversions", description = "Convert measurements between supported units")
public class ConversionController {

    private final ConversionService conversionService;

    ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a numeric measurement from a source unit to a target unit")
    ConversionResult convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request);
    }
}
