package com.edgarkirk.unitconv.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

    @Query("select u from Unit u where lower(u.name) = lower(:name) order by u.id desc")
    java.util.List<Unit> findByNameIgnoreCase(@Param("name") String name, Pageable pageable);

    default Optional<Unit> findByNameIgnoreCase(String name) {
        return findByNameIgnoreCase(name, PageRequest.of(0, 1)).stream().findFirst();
    }
}
