package com.edgarkirk.unitconv.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "unit")
public class Unit {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String system;

    protected Unit() {
    }

    public Unit(UUID id, String name, String system) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit unit = (Unit) o;
        return Objects.equals(name, unit.name) && Objects.equals(system, unit.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, system);
    }
}
