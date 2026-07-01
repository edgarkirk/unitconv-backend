package com.edgarkirk.unitconv.persistence.entity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversion_results")
public class ConversionResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal inputValue;

    @Column(nullable = false, length = 50)
    private String sourceUnit;

    @Column(nullable = false, length = 50)
    private String targetUnit;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal result;

    protected ConversionResultEntity() {
    }

    public ConversionResultEntity(BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ConversionResultEntity that = (ConversionResultEntity) other;
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
