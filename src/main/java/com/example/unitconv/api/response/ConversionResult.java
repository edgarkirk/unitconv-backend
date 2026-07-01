package com.example.unitconv.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ConversionResult(
    UUID id,
    BigDecimal inputValue,
    String sourceUnit,
    String targetUnit,
    BigDecimal result
) {
}
