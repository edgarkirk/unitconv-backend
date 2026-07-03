package com.edgarkirk.unitconv.mapper;

import com.edgarkirk.unitconv.api.dto.response.Unit;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

    public List<Unit> toResponses(List<com.edgarkirk.unitconv.persistence.entity.Unit> units) {
        return units.stream()
                .map(unit -> new Unit(unit.getId(), unit.getName(), unit.getSystem()))
                .toList();
    }
}
