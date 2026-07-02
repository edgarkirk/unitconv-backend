package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UnitController.class)
@SuppressWarnings({"removal", "deprecation"})
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionService conversionService;

    @Test
    void should_return200AndUnitsArray_when_getUnits() throws Exception {
        when(conversionService.getUnits()).thenReturn(List.of(
                new UnitResponse(UUID.randomUUID(), "feet", "imperial"),
                new UnitResponse(UUID.randomUUID(), "metres", "metric")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$[0].name").value("feet"))
                .andExpect(jsonPath("$[0].system").value("imperial"));
    }
}
