package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new ConversionServiceImpl(unitRepository, conversionResultRepository);
    }

    @Test
    void should_convertMetresToFeet_when_requestIsValid() {
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1.0"), "metres", "feet")))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("not implemented");
    }

    @Test
    void should_rejectIncompatibleUnits_when_unitsAreFromDifferentSystems() {
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1.0"), "metres", "gallons")))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("not implemented");
    }

    @Test
    void should_listSupportedUnits_when_called() {
        assertThatThrownBy(() -> conversionService.getSupportedUnits())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("not implemented");
    }
}
