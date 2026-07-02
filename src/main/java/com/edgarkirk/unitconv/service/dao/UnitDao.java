package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UnitDao {

    private final UnitRepository unitRepository;

    public UnitDao(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public Optional<Unit> findByName(String name) {
        return unitRepository.findByName(name);
    }

    public List<Unit> findAllByOrderByNameAsc() {
        return unitRepository.findAllByOrderByNameAsc();
    }
}
