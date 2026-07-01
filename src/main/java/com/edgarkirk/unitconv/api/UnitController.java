package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.UnitService;
import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
class UnitController {

    private final UnitService unitService;

    UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("/units")
    List<Unit> listSupportedUnits() {
        return unitService.findSupportedUnits();
    }
}
