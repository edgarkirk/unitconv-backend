package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnitDao {

    private static final Logger log = LoggerFactory.getLogger(UnitDao.class);

    private final UnitRepository unitRepository;

    public UnitDao(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> findAll() {
        log.info("Fetching all supported units");
        return unitRepository.findAll();
    }

    public Optional<Unit> findByName(String name) {
        return unitRepository.findByName(name);
    }
}
