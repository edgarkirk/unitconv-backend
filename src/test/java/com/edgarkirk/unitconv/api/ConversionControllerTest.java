package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    void should_returnCreatedConversion_when_validRequestIsSubmitted() throws Exception {
        when(conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "feet")))
                .thenReturn(new ConversionResult(UUID.fromString("11111111-1111-1111-1111-111111111111"), new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084")));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(32.8084));
    }

    @Test
    void should_return400ValidationError_when_unitsAreIncompatible() throws Exception {
        doThrow(new IncompatibleUnitsException("metres", "gallons"))
                .when(conversionService)
                .convert(any(ConversionRequest.class));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres cannot be converted to gallons"))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void should_return400ValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value: value must be a number"));
    }

    @Test
    void should_return400ValidationError_when_requiredFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_returnAllSupportedUnits_when_gettingUnits() throws Exception {
        when(conversionService.listUnits()).thenReturn(List.of(
                new Unit(UUID.fromString("22222222-2222-2222-2222-222222222222"), "metres", "metric"),
                new Unit(UUID.fromString("33333333-3333-3333-3333-333333333333"), "feet", "imperial")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("22222222-2222-2222-2222-222222222222"))
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"));
    }
}
