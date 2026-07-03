package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String system;

    protected Unit() {
    }

    public Unit(UUID id, String name, String system) {
        this.id = id;
        this.name = name;
        this.system = system;
    }

    public Unit(String name, String system) {
        this(null, name, system);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Unit unit)) {
            return false;
        }
        return id != null && id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
