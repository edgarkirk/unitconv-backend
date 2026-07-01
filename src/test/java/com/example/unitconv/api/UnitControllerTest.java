package com.example.unitconv.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.example.unitconv.api.controller.UnitController;
import com.example.unitconv.api.response.UnitResponse;
import com.example.unitconv.application.ConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@WebMvcTest(UnitController.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnSupportedUnits_when_requestingUnits() throws Exception {
        given(conversionService.units()).willReturn(List.of(
            new UnitResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), "metres", "metric"),
            new UnitResponse(UUID.fromString("22222222-2222-2222-2222-222222222222"), "feet", "imperial")
        ));

        mockMvc.perform(get("/api/units"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("metres"))
            .andExpect(jsonPath("$[0].system").value("metric"))
            .andExpect(jsonPath("$[1].name").value("feet"))
            .andExpect(jsonPath("$[1].system").value("imperial"));
    }
}
