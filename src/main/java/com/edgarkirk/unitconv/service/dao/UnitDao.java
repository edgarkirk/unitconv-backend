package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnitDao {

    private final UnitRepository unitRepository;

    public UnitDao(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> findAll() {
        throw new UnsupportedOperationException("UnitDao.findAll is not implemented yet");
    }

    public Optional<Unit> findByName(String name) {
        throw new UnsupportedOperationException("UnitDao.findByName is not implemented yet");
    }
}
