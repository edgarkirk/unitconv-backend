package com.edgarkirk.unitconv.persistence.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "units")
public class UnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String system;

    protected UnitEntity() {
    }

    public UnitEntity(String name, String system) {
        this.name = name;
        this.system = system;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSystem() {
        return system;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UnitEntity that = (UnitEntity) other;
        return Objects.equals(name, that.name) && Objects.equals(system, that.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, system);
    }
}
