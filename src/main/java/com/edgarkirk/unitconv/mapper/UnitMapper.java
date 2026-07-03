package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.Unit;

public final class UnitMapper {

    private UnitMapper() {
    }

    public static UnitResponse toResponse(Unit unit) {
        return new UnitResponse(unit.id(), unit.name(), unit.system());
    }
}
