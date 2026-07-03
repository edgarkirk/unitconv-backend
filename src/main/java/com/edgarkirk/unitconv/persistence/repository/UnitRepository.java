package com.edgarkirk.unitconv.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitEntity, UUID> {

    List<UnitEntity> findAllByOrderByNameAsc();

    Optional<UnitEntity> findByName(String name);
}
