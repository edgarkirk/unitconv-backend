package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversionResultRepository extends JpaRepository<ConversionResult, UUID> {
}
