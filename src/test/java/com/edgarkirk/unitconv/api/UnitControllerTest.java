package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.UnitService;

@WebMvcTest(controllers = UnitController.class)
@Import(UnitControllerTest.MockConfig.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UnitService unitService;

    @Test
    void should_returnSupportedUnits_when_requestIsValid() throws Exception {
        when(unitService.findAll()).thenReturn(List.of(
                new UnitResponse(UUID.randomUUID(), "metres", "metric"),
                new UnitResponse(UUID.randomUUID(), "feet", "imperial"),
                new UnitResponse(UUID.randomUUID(), "kilometres", "metric"),
                new UnitResponse(UUID.randomUUID(), "miles", "imperial"),
                new UnitResponse(UUID.randomUUID(), "litres", "metric"),
                new UnitResponse(UUID.randomUUID(), "gallons", "imperial")));

        mockMvc.perform(get("/api/units").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        UnitService unitService() {
            return Mockito.mock(UnitService.class);
        }
    }
}
