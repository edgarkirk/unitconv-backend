package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.response.Unit;
import com.edgarkirk.unitconv.application.ConversionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UnitController.class)
@Import(com.edgarkirk.unitconv.api.advice.GlobalExceptionHandler.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void lists_supported_units_successfully() throws Exception {
        // Arrange
        given(conversionService.listSupportedUnits()).willReturn(List.of(
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000001"), "feet", "imperial"),
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000002"), "gallons", "imperial"),
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000003"), "kilometres", "metric"),
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000004"), "litres", "metric"),
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000005"), "metres", "metric"),
                new Unit(UUID.fromString("00000000-0000-0000-0000-000000000006"), "miles", "imperial")));

        // Act & Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(6)))
                .andExpect(jsonPath("$[0].name").value("feet"))
                .andExpect(jsonPath("$[1].name").value("gallons"))
                .andExpect(jsonPath("$[2].name").value("kilometres"))
                .andExpect(jsonPath("$[3].name").value("litres"))
                .andExpect(jsonPath("$[4].name").value("metres"))
                .andExpect(jsonPath("$[5].name").value("miles"));
    }
}
