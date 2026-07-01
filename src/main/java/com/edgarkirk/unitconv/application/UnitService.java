package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;

public interface UnitService {

    List<Unit> findSupportedUnits();
}
