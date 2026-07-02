package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class UnitController {

    private final ConversionService conversionService;

    UnitController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/units")
    List<UnitResponse> getUnits() {
        return conversionService.getUnits();
    }
}
