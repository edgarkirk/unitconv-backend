package com.edgarkirk.unitconv.service.dao;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitDaoTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitDao unitDao;

    @Test
    void should_delegateFindByNameToRepository() {
        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setName("metres");
        unit.setSystem("metric");
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(unit));

        Optional<Unit> result = unitDao.findByName("metres");

        assertThat(result).contains(unit);
        verify(unitRepository).findByName("metres");
    }

    @Test
    void should_delegateFindAllOrderedToRepository() {
        when(unitRepository.findAllByOrderByNameAsc()).thenReturn(List.of());

        unitDao.findAllByOrderByNameAsc();

        verify(unitRepository).findAllByOrderByNameAsc();
    }
}
