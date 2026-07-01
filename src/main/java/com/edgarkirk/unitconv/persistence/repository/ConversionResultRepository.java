package com.edgarkirk.unitconv.persistence.repository;

import java.util.UUID;

import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionResultRepository extends JpaRepository<ConversionResultEntity, UUID> {
}
