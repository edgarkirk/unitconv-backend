package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.impl.UnitServiceImpl;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void should_returnSupportedUnits_when_repositoryContainsSeedData() {
        when(unitRepository.findAll()).thenReturn(List.of(
                unit("metres", "metric"),
                unit("feet", "imperial"),
                unit("kilometres", "metric"),
                unit("miles", "imperial"),
                unit("litres", "metric"),
                unit("gallons", "imperial")));

        List<UnitResponse> units = unitService.findAll();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(UnitResponse::name)
                .containsExactly("metres", "feet", "kilometres", "miles", "litres", "gallons");
    }

    @Test
    void should_returnMetricAndImperialSystems_when_mappedFromRepositoryEntities() {
        when(unitRepository.findAll()).thenReturn(List.of(
                unit("metres", "metric"),
                unit("feet", "imperial")));

        List<UnitResponse> units = unitService.findAll();

        assertThat(units).extracting(UnitResponse::system).containsExactly("metric", "imperial");
    }

    private UnitEntity unit(String name, String system) {
        UnitEntity entity = new UnitEntity(name, system);
        entity.setId(UUID.randomUUID());
        return entity;
    }
}
