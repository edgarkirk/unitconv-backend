package com.edgarkirk.unitconv.api;

import java.util.List;

import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/units")
@Tag(name = "Units", description = "Supported unit listing endpoint")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @Operation(summary = "List supported units and their systems")
    @GetMapping
    public List<Unit> listUnits() {
        return unitService.listUnits();
    }
}
