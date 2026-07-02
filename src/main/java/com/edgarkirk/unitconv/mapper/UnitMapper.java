package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.api.dto.response.Unit;

public final class UnitMapper {

    private UnitMapper() {
    }

    public static Unit toResponse(com.edgarkirk.unitconv.persistence.entity.Unit entity) {
        return new Unit(entity.getId(), entity.getName(), entity.getSystem());
    }
}
