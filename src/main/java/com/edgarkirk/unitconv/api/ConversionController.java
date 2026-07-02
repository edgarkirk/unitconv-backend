package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Conversion API", description = "Measurement conversion endpoints")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a numeric measurement from a source unit to a target unit")
    public ResponseEntity<ConversionResult> convert(@Valid @RequestBody ConversionRequest request) {
        throw new UnsupportedOperationException("ConversionController.convert is not implemented yet");
    }

    @GetMapping("/units")
    @Operation(summary = "List supported units and their systems (metric/imperial)")
    public ResponseEntity<List<Unit>> listUnits() {
        throw new UnsupportedOperationException("ConversionController.listUnits is not implemented yet");
    }
}
