package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.Unit;

public final class UnitMapper {

    private UnitMapper() {
    }

    public static UnitResponse toResponse(Unit unit) {
        return new UnitResponse(unit.getId(), unit.getName(), unit.getSystem());
    }
}
