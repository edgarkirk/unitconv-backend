package com.edgarkirk.unitconv.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "unit")
public class Unit {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String system;

    protected Unit() {
    }

    public Unit(final String name, final String system) {
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final Unit unit = (Unit) other;
        return Objects.equals(name, unit.name) && Objects.equals(system, unit.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, system);
    }
}
