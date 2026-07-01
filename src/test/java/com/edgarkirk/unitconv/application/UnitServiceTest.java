package com.edgarkirk.unitconv.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;
import org.junit.jupiter.api.Test;

class UnitServiceTest {

    private final UnitCatalog unitCatalog = new InMemoryUnitCatalog();

    @Test
    void should_listSupportedUnits_when_requested() {
        DefaultUnitService service = new DefaultUnitService(unitCatalog);

        List<Unit> units = service.findSupportedUnits();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(Unit::name)
                .containsExactly("metres", "feet", "kilometres", "miles", "litres", "gallons");
        assertThat(units).extracting(Unit::system)
                .containsExactly("metric", "imperial", "metric", "imperial", "metric", "imperial");
    }
}
