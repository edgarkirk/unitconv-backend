package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionResultRepository extends JpaRepository<ConversionResult, UUID> {
}
