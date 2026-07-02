package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversionResultRepository extends JpaRepository<ConversionResult, UUID> {
}
