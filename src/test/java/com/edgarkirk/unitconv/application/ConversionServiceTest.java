package com.edgarkirk.unitconv.application;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> {
            com.edgarkirk.unitconv.persistence.entity.ConversionResult entity = invocation.getArgument(0);
            return new com.edgarkirk.unitconv.persistence.entity.ConversionResult(
                    UUID.fromString("00000000-0000-0000-0000-000000000321"),
                    entity.getInputValue(),
                    entity.getSourceUnit(),
                    entity.getTargetUnit(),
                    entity.getResult());
        });

        ConversionResult result = conversionService.convert(new ConversionRequest(new BigDecimal("1.0"), "metres", "feet"));

        assertThat(result.id()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000321"));
        assertThat(result.inputValue()).isEqualByComparingTo("1.0");
        assertThat(result.sourceUnit()).isEqualTo("metres");
        assertThat(result.targetUnit()).isEqualTo("feet");
        assertThat(result.result()).isEqualByComparingTo("3.280840000000");

        ArgumentCaptor<com.edgarkirk.unitconv.persistence.entity.ConversionResult> captor = ArgumentCaptor.forClass(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class);
        verify(conversionResultRepository).save(captor.capture());
        assertThat(captor.getValue().getInputValue()).isEqualByComparingTo("1.0");
        assertThat(captor.getValue().getResult()).isEqualByComparingTo("3.280840000000");
    }

    @Test
    void should_rejectUnsupportedSameGroupPair_when_unitsAreNotASupportedPair() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("kilometres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "kilometres", "metric")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1.0"), "metres", "kilometres")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessageContaining("incompatible");
    }

    @Test
    void should_listSupportedUnits_when_called() {
        when(unitRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "feet", "imperial"),
                new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));

        List<Unit> units = conversionService.getSupportedUnits();

        assertThat(units).extracting(Unit::name).containsExactly("feet", "metres");
        assertThat(units).extracting(Unit::system).containsExactly("imperial", "metric");
    }
}
