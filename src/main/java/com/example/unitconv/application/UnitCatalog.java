package com.example.unitconv.application;

import java.util.List;
import java.util.Optional;

interface UnitCatalog {

    List<UnitDefinition> allUnits();

    Optional<UnitDefinition> findByName(String name);
}
