package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UnitDaoImpl implements UnitDao {

    private final UnitRepository unitRepository;

    public UnitDaoImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> findAllSupportedUnits() {
        return unitRepository.findAll();
    }

    @Override
    public Optional<Unit> findByName(String name) {
        return unitRepository.findByName(name);
    }
}
