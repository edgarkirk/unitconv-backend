package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class InMemoryUnitCatalog implements UnitCatalog {

    private static final List<Unit> SUPPORTED_UNITS = List.of(
            new Unit(UUID.fromString("11111111-1111-1111-1111-111111111111"), "metres", "metric"),
            new Unit(UUID.fromString("22222222-2222-2222-2222-222222222222"), "feet", "imperial"),
            new Unit(UUID.fromString("33333333-3333-3333-3333-333333333333"), "kilometres", "metric"),
            new Unit(UUID.fromString("44444444-4444-4444-4444-444444444444"), "miles", "imperial"),
            new Unit(UUID.fromString("55555555-5555-5555-5555-555555555555"), "litres", "metric"),
            new Unit(UUID.fromString("66666666-6666-6666-6666-666666666666"), "gallons", "imperial"));

    @Override
    public List<Unit> supportedUnits() {
        return SUPPORTED_UNITS;
    }
}
