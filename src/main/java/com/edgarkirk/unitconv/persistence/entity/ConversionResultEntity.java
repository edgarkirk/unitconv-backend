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
@Table(name = "conversion_results")
public class ConversionResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal inputValue;

    @Column(nullable = false)
    private String sourceUnit;

    @Column(nullable = false)
    private String targetUnit;

    @Column(nullable = false)
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
}
