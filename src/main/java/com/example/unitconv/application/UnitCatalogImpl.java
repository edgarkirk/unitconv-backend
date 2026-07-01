package com.example.unitconv.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
class UnitCatalogImpl implements UnitCatalog {

    @Override
    public List<UnitDefinition> allUnits() {
        return List.of();
    }

    @Override
    public Optional<UnitDefinition> findByName(String name) {
        return Optional.empty();
    }
}
