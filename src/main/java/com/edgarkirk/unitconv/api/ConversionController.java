package com.edgarkirk.unitconv.api;

import java.util.List;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Conversion", description = "Unit conversion operations")
@Validated
@RestController
@RequestMapping("/api")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Operation(summary = "Convert a numeric measurement from a source unit to a target unit")
    @PostMapping("/convert")
    public ConversionResultResponse convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request);
    }

    @Operation(summary = "List supported units and their systems")
    @GetMapping("/units")
    public List<UnitResponse> units() {
        return conversionService.listSupportedUnits();
    }
}
