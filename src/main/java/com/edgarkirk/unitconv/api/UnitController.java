package com.edgarkirk.unitconv.api;

import java.util.List;

import com.edgarkirk.unitconv.api.response.Unit;
import com.edgarkirk.unitconv.application.ConversionService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api")
class UnitController {

    private final ConversionService conversionService;

    UnitController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/units")
    public List<Unit> listUnits() {
        return conversionService.listSupportedUnits();
    }
}
