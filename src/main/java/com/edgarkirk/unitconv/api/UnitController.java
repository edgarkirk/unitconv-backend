package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.UnitService;
import com.edgarkirk.unitconv.dto.response.Unit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Units", description = "Supported unit catalogue")
class UnitController {

    private final UnitService unitService;

    UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("/units")
    @Operation(summary = "List supported units and their systems (metric/imperial)")
    List<Unit> listSupportedUnits() {
        return unitService.findSupportedUnits();
    }
}
