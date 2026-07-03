package com.edgarkirk.unitconv.api;

import static com.edgarkirk.unitconv.TestFixtures.conversionResultResponse;
import static com.edgarkirk.unitconv.TestFixtures.supportedUnits;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_return200_when_validConversionSubmitted() throws Exception {
        when(conversionService.convert(any())).thenReturn(
                conversionResultResponse(new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084")));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(32.8084));
    }

    @Test
    void should_return400_when_crossGroupUnitsProvided() throws Exception {
        when(conversionService.convert(any())).thenThrow(new IncompatibleUnitsException("Incompatible units: metres and gallons"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"gallons\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_when_invalidNumericValueProvided() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value for field value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_valueMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_sourceUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_when_targetUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_returnAllSixUnits_when_getUnits() throws Exception {
        when(conversionService.listUnits()).thenReturn(supportedUnits());

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("feet"))
                .andExpect(jsonPath("$[1].name").value("gallons"))
                .andExpect(jsonPath("$[2].name").value("kilometres"))
                .andExpect(jsonPath("$[3].name").value("litres"))
                .andExpect(jsonPath("$[4].name").value("metres"))
                .andExpect(jsonPath("$[5].name").value("miles"));
    }
}
