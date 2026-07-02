package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UnitController {

    private final ConversionService conversionService;

    public UnitController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/units")
    public List<UnitResponse> getUnits() {
        return conversionService.getUnits();
    }
}
