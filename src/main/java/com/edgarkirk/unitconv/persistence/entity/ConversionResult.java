package com.edgarkirk.unitconv.persistence.entity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "conversion_results")
public class ConversionResult {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "input_value", nullable = false, precision = 19, scale = 6)
    private BigDecimal inputValue;

    @Column(name = "source_unit", nullable = false, length = 255)
    private String sourceUnit;

    @Column(name = "target_unit", nullable = false, length = 255)
    private String targetUnit;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal result;

    protected ConversionResult() {
    }

    public ConversionResult(BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
        this.inputValue = inputValue;
        this.sourceUnit = sourceUnit;
        this.targetUnit = targetUnit;
        this.result = result;
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
        if (!(o instanceof ConversionResult that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}
