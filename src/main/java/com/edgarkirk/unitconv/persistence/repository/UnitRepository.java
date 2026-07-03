package com.edgarkirk.unitconv.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edgarkirk.unitconv.persistence.entity.UnitEntity;

public interface UnitRepository extends JpaRepository<UnitEntity, UUID> {

    @Override
    @Query("""
            select u from UnitEntity u
            order by case
                when u.name = 'metres' then 1
                when u.name = 'feet' then 2
                when u.name = 'kilometres' then 3
                when u.name = 'miles' then 4
                when u.name = 'litres' then 5
                when u.name = 'gallons' then 6
                else 7
            end
            """)
    List<UnitEntity> findAll();

    Optional<UnitEntity> findByName(String name);
}
