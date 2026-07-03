package com.edgarkirk.unitconv.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edgarkirk.unitconv.persistence.entity.UnitEntity;

public interface UnitRepository extends JpaRepository<UnitEntity, UUID> {

    Optional<UnitEntity> findByName(String name);
}
