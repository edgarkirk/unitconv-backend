package com.edgarkirk.unitconv.service.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnitDaoTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitDaoImpl unitDao;

    @Test
    void should_returnAllUnits_when_repositoryProvidesSeededUnits() {
        // Arrange
        List<Unit> units = List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial"));
        when(unitRepository.findAll()).thenReturn(units);

        // Act
        List<Unit> actual = unitDao.findAllSupportedUnits();

        // Assert
        assertThat(actual).isEqualTo(units);
        verify(unitRepository).findAll();
    }

    @Test
    void should_returnUnit_when_repositoryFindsUnitByName() {
        // Arrange
        Unit unit = new Unit(UUID.randomUUID(), "feet", "imperial");
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(unit));

        // Act
        Optional<Unit> actual = unitDao.findByName("feet");

        // Assert
        assertThat(actual).contains(unit);
        verify(unitRepository).findByName("feet");
    }
}
