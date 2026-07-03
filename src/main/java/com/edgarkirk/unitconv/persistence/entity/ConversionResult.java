package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "conversion_result")
public class ConversionResult {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal inputValue;

    @Column(nullable = false)
    private String sourceUnit;

    @Column(nullable = false)
    private String targetUnit;

    @Column(nullable = false, precision = 38, scale = 16)
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

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
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
}
