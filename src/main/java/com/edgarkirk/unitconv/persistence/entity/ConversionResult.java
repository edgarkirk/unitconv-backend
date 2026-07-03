package com.edgarkirk.unitconv.persistence.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversion_result")
public class ConversionResult {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "input_value", nullable = false, precision = 38, scale = 18)
    private BigDecimal inputValue;

    @Column(name = "source_unit", nullable = false)
    private String sourceUnit;

    @Column(name = "target_unit", nullable = false)
    private String targetUnit;

    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal result;

    protected ConversionResult() {
    }

    public ConversionResult(UUID id, BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
        this.id = id;
        this.inputValue = inputValue;
        this.sourceUnit = sourceUnit;
        this.targetUnit = targetUnit;
        this.result = result;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getInputValue() {
        return inputValue;
    }

    public String getSourceUnit() {
        return sourceUnit;
    }

    public String getTargetUnit() {
        return targetUnit;
    }

    public BigDecimal getResult() {
        return result;
    }

    public UUID id() {
        return id;
    }

    public BigDecimal inputValue() {
        return inputValue;
    }

    public String sourceUnit() {
        return sourceUnit;
    }

    public String targetUnit() {
        return targetUnit;
    }

    public BigDecimal result() {
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConversionResult that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
