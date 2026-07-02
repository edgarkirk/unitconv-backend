package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversion_results")
public class ConversionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "input_value", nullable = false, precision = 19, scale = 6)
    private BigDecimal inputValue;

    @Column(name = "source_unit", nullable = false)
    private String sourceUnit;

    @Column(name = "target_unit", nullable = false)
    private String targetUnit;

    @Column(name = "result", nullable = false, precision = 19, scale = 6)
    private BigDecimal result;

    public ConversionResult() {
    }

    public static ConversionResult create() {
        return new ConversionResult();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getInputValue() {
        return inputValue;
    }

    public void setInputValue(BigDecimal inputValue) {
        this.inputValue = inputValue;
    }

    public String getSourceUnit() {
        return sourceUnit;
    }

    public void setSourceUnit(String sourceUnit) {
        this.sourceUnit = sourceUnit;
    }

    public String getTargetUnit() {
        return targetUnit;
    }

    public void setTargetUnit(String targetUnit) {
        this.targetUnit = targetUnit;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversionResult that = (ConversionResult) o;
        return Objects.equals(inputValue, that.inputValue)
                && Objects.equals(sourceUnit, that.sourceUnit)
                && Objects.equals(targetUnit, that.targetUnit)
                && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputValue, sourceUnit, targetUnit, result);
    }
}
