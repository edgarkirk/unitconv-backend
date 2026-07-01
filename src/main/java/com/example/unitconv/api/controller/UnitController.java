package com.example.unitconv.api.controller;

import java.util.List;

import com.example.unitconv.api.response.UnitResponse;
import com.example.unitconv.application.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Units", description = "Supported measurement units")
public class UnitController {

    private final ConversionService conversionService;

    UnitController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/units")
    @Operation(summary = "List supported units and their systems")
    List<UnitResponse> units() {
        return conversionService.units();
    }
}
