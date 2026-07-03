package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversion_result")
public class ConversionResultEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "input_value", nullable = false, precision = 19, scale = 6)
    private BigDecimal inputValue;

    @Column(name = "source_unit", nullable = false, length = 255)
    private String sourceUnit;

    @Column(name = "target_unit", nullable = false, length = 255)
    private String targetUnit;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal result;

    protected ConversionResultEntity() {
    }

    public ConversionResultEntity(UUID id, BigDecimal inputValue, String sourceUnit, String targetUnit, BigDecimal result) {
        this.id = id;
        this.inputValue = inputValue;
        this.sourceUnit = sourceUnit;
        this.targetUnit = targetUnit;
        this.result = result;
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
        if (!(o instanceof ConversionResultEntity that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}
