package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitPairException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ConversionController.class)
@Import(ApiExceptionHandler.class)
@SuppressWarnings("deprecation")
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnCreatedConversion_when_validInput() throws Exception {
        when(conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "feet")))
                .thenReturn(new ConversionResultResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), new BigDecimal("1"), "metres", "feet", new BigDecimal("3.280839895013123")));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConversionRequest(new BigDecimal("1"), "metres", "feet"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.inputValue").value(1))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(3.280839895013123d));
    }

    @Test
    void should_returnAllUnits_when_getUnits() throws Exception {
        when(conversionService.listSupportedUnits()).thenReturn(List.of(
                new UnitResponse(UUID.fromString("22222222-2222-2222-2222-222222222222"), "feet", "imperial"),
                new UnitResponse(UUID.fromString("33333333-3333-3333-3333-333333333333"), "metres", "metric")));

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("feet"))
                .andExpect(jsonPath("$[1].system").value("metric"));
    }

    @Test
    void should_return400_when_incompatibleUnits() throws Exception {
        when(conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "gallons")))
                .thenThrow(new IncompatibleUnitPairException("Units metres and gallons are incompatible"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConversionRequest(new BigDecimal("1"), "metres", "gallons"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Units metres and gallons are incompatible"))
                .andExpect(jsonPath("$.field").value(nullValue()));
    }

    @Test
    void should_return400_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value must be numeric"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_valueMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value is required"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_sourceUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":1,\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("sourceUnit is required"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_when_targetUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":1,\"sourceUnit\":\"metres\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("targetUnit is required"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_return400_when_jsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":1,\"sourceUnit\":\"metres\",\"targetUnit\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid JSON payload"))
                .andExpect(jsonPath("$.field").value(nullValue()));
    }
}
