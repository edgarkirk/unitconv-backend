package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import java.util.List;
import java.util.Optional;

public interface UnitDao {

    List<Unit> findAllSupportedUnits();

    Optional<Unit> findByName(String name);
}
