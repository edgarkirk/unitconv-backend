package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
class ConversionController {

    private final ConversionService conversionService;

    ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    ConversionResult convert(@Valid @RequestBody ConversionRequest request) {
        throw new UnsupportedOperationException("Conversion endpoint is not implemented yet");
    }

    @GetMapping("/units")
    List<Unit> getUnits() {
        throw new UnsupportedOperationException("Units endpoint is not implemented yet");
    }
}
