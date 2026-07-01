package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Conversion", description = "Measurement conversion operations")
class ConversionController {

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
