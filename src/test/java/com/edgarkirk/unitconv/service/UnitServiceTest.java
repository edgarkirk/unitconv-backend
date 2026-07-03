package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void should_return_all_supported_units_when_units_are_loaded() {
        // Arrange
        when(unitRepository.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric"),
                new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "feet", "imperial")));

        // Act
        List<Unit> units = unitService.listUnits();

        // Assert
        assertThat(units).extracting(Unit::name).containsExactly("metres", "feet");
        assertThat(units).extracting(Unit::system).containsExactly("metric", "imperial");
    }
}
