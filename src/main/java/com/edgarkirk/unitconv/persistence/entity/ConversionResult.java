package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversion_result")
public class ConversionResult {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 12)
    private BigDecimal inputValue;

    @Column(nullable = false, length = 50)
    private String sourceUnit;

    @Column(nullable = false, length = 50)
    private String targetUnit;

    @Column(nullable = false, precision = 19, scale = 12)
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
