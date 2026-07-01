package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.dto.response.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnCreatedConversion_when_payloadIsValid() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1.0,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(1.0))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(3.28084));
    }

    @Test
    void should_returnValidationError_when_unitsAreIncompatible() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1.0,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("incompatible")));
    }

    @Test
    void should_returnValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("numeric")));
    }

    @Test
    void should_returnUnits_when_requested() throws Exception {
        when(conversionService.getSupportedUnits()).thenReturn(List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[1].system").value("imperial"));
    }
}
