package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Conversion API", description = "Measurement conversion endpoints")
class ConversionController {

    private final ConversionService conversionService;

    ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a numeric measurement from a source unit to a target unit")
    public ConversionResult convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request);
    }

    @GetMapping("/units")
    @Operation(summary = "List supported units and their systems (metric/imperial)")
    public List<Unit> listUnits() {
        return conversionService.listUnits();
    }
}
