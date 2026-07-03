package com.edgarkirk.unitconv.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;

public interface ConversionResultRepository extends JpaRepository<ConversionResultEntity, UUID> {
}
