package com.edgarkirk.unitconv.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

    Optional<Unit> findByNameIgnoreCase(String name);
}
