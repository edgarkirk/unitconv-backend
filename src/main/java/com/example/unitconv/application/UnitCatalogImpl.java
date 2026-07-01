package com.example.unitconv.application;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
class UnitCatalogImpl implements UnitCatalog {

    private static final List<UnitDefinition> UNITS = List.of(
        new UnitDefinition("metres", "metric", "metres-feet", BigDecimal.ONE),
        new UnitDefinition("feet", "imperial", "metres-feet", new BigDecimal("3.28084")),
        new UnitDefinition("kilometres", "metric", "kilometres-miles", BigDecimal.ONE),
        new UnitDefinition("miles", "imperial", "kilometres-miles", new BigDecimal("0.621371")),
        new UnitDefinition("litres", "metric", "litres-gallons", BigDecimal.ONE),
        new UnitDefinition("gallons", "imperial", "litres-gallons", new BigDecimal("0.264172"))
    );

    @Override
    public List<UnitDefinition> allUnits() {
        return UNITS;
    }

    @Override
    public Optional<UnitDefinition> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return UNITS.stream()
            .filter(unit -> unit.name().equals(normalized))
            .findFirst();
    }

    static UUID unitId(String name) {
        return UUID.nameUUIDFromBytes(("unitconv:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
