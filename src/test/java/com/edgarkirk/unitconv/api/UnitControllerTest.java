package com.edgarkirk.unitconv.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.application.UnitService;
import com.edgarkirk.unitconv.dto.response.Unit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;

@WebMvcTest(UnitController.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitService unitService;

    @Test
    void should_returnOk_when_supportedUnitsAreRequested() throws Exception {
        when(unitService.findSupportedUnits()).thenReturn(List.of(
                new Unit(UUID.fromString("11111111-1111-1111-1111-111111111111"), "metres", "metric")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"));
    }
}
