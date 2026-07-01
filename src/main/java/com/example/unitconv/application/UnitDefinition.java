package com.example.unitconv.application;

import java.math.BigDecimal;

record UnitDefinition(
    String name,
    String system,
    String group,
    BigDecimal ratioToBaseUnit
) {
}
