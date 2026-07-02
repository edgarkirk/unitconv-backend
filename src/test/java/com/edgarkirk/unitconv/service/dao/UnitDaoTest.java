package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitDaoTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitDao unitDao;

    @Test
    void should_returnAllUnits_when_repositoryFindAllReturnsData() {
        when(unitRepository.findAll()).thenReturn(List.of(new Unit("metres", "metric"), new Unit("feet", "imperial")));

        List<Unit> units = unitDao.findAll();

        assertThat(units).hasSize(2);
    }

    @Test
    void should_returnUnit_when_repositoryFindByNameReturnsMatch() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit("metres", "metric")));

        Optional<Unit> unit = unitDao.findByName("metres");

        assertThat(unit).isPresent();
    }
}
