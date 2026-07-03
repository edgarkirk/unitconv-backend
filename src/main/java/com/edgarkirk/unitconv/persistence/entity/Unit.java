package com.edgarkirk.unitconv.persistence.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String system;

    protected Unit() {
    }

    public Unit(String name, String system) {
        this.name = name;
        this.system = system;
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
        if (!(o instanceof Unit unit)) {
            return false;
        }
        return id != null && id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}
