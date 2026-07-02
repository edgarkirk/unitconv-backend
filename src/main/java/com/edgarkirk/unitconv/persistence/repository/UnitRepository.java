package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    Optional<Unit> findByName(String name);
}
