package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DefaultUnitService implements UnitService {

    private final UnitCatalog unitCatalog;

    DefaultUnitService(UnitCatalog unitCatalog) {
        this.unitCatalog = unitCatalog;
    }

    @Override
    public List<Unit> findSupportedUnits() {
        return unitCatalog.supportedUnits();
    }
}
