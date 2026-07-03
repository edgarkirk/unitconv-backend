package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.config.GlobalExceptionHandler;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
@Import(GlobalExceptionHandler.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_return200AndConversionResult_when_convertRequestIsValid() throws Exception {
        when(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new ConversionResultResponse(UUID.randomUUID(), BigDecimal.valueOf(12.5), "metres", "feet", BigDecimal.valueOf(41.0105)));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":12.5,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"));
    }

    @Test
    void should_return400AndValidationError_when_valueFieldMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400AndValidationError_when_nonNumericValueProvided() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value"));
    }

    @Test
    void should_return400AndValidationError_when_serviceRejectsIncompatibleUnits() throws Exception {
        when(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IncompatibleUnitException("Incompatible units: metres to gallons"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres to gallons"));
    }

    @Test
    void should_return200AndUnits_when_getUnits() throws Exception {
        when(conversionService.listUnits()).thenReturn(List.of(
                new UnitResponse(UUID.randomUUID(), "metres", "metric"),
                new UnitResponse(UUID.randomUUID(), "feet", "imperial")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[1].system").value("imperial"));
    }
}
