package com.edgarkirk.unitconv.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.UnitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UnitController.class)
@ActiveProfiles("test")
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitService unitService;

    @Test
    void should_return200_when_listing_supported_units() throws Exception {
        // Arrange
        List<Unit> units = List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial"));
        org.mockito.Mockito.when(unitService.listUnits()).thenReturn(units);

        // Act & Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"))
                .andExpect(jsonPath("$[1].name").value("feet"))
                .andExpect(jsonPath("$[1].system").value("imperial"));
    }

    @Test
    void should_return405_when_attempting_to_create_units() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/units")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }
}
