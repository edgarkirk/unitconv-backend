package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.mapper.ConversionResultMapper;
import com.edgarkirk.unitconv.mapper.UnitMapper;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    private ConversionServiceImpl conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new ConversionServiceImpl(
                unitRepository,
                conversionResultRepository,
                new ConversionResultMapper(),
                new UnitMapper());
    }

    @Test
    void should_convertMetresToFeet_and_persistResult_when_requestIsValid() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any(ConversionResult.class)))
                .thenAnswer(invocation -> {
                    ConversionResult saved = invocation.getArgument(0);
                    return new ConversionResult(UUID.randomUUID(), saved.getInputValue(), saved.getSourceUnit(), saved.getTargetUnit(), saved.getResult());
                });

        var response = conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "feet"));

        assertThat(response.id()).isNotNull();
        assertThat(response.inputValue()).isEqualByComparingTo("10");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isCloseTo(new BigDecimal("32.8083989501"), within(new BigDecimal("0.0000001")));
    }

    @Test
    void should_returnSupportedUnits_when_listingUnits() {
        when(unitRepository.findAll()).thenReturn(List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial")));

        assertThat(conversionService.getSupportedUnits())
                .extracting(unit -> unit.name())
                .containsExactlyInAnyOrder("metres", "feet");
    }

    @Test
    void should_rejectIncompatibleUnits_when_crossGroupConversionRequested() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "gallons", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitException.class)
                .hasMessageContaining("Incompatible units: metres and gallons");
    }
}
