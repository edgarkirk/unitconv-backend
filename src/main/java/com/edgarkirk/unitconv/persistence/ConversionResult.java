package com.edgarkirk.unitconv.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "conversion_result")
public class ConversionResult {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private BigDecimal inputValue;

    @Column(nullable = false)
    private String sourceUnit;

    @Column(nullable = false)
    private String targetUnit;

    @Column(nullable = false)
    private BigDecimal result;

    protected ConversionResult() {
    }

    public ConversionResult(final BigDecimal inputValue, final String sourceUnit, final String targetUnit, final BigDecimal result) {
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final ConversionResult that = (ConversionResult) other;
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
