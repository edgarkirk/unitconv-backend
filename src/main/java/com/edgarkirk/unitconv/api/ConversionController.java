package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Conversion API", description = "Unit conversion and supported units endpoints")
class ConversionController {

    private final ConversionService conversionService;

    ConversionController(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    @PostMapping("/convert")
    @Operation(summary = "Convert a numeric measurement from a source unit to a target unit")
    ResponseEntity<ConversionResult> convert(@Valid @RequestBody final ConversionRequest request) {
        return ResponseEntity.ok(conversionService.convert(request));
    }

    @GetMapping("/units")
    @Operation(summary = "List supported units and their systems (metric/imperial)")
    ResponseEntity<List<Unit>> listUnits() {
        return ResponseEntity.ok(conversionService.listUnits());
    }
}
