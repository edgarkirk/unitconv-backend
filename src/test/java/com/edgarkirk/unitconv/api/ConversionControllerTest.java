package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.service.ConversionService;

@WebMvcTest(controllers = ConversionController.class)
@Import(ConversionControllerTest.MockConfig.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversionService conversionService;

    @Test
    void should_returnConvertedResult_when_requestIsValid() throws Exception {
        when(conversionService.convert(any(ConversionRequest.class))).thenReturn(
                new ConversionResultResponse(UUID.randomUUID(), new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084")));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(closeTo(32.8084, 0.000001)));
    }

    @Test
    void should_returnValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid numeric value")))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_returnValidationError_when_sourceUnitIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("sourceUnit")))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        ConversionService conversionService() {
            return Mockito.mock(ConversionService.class);
        }
    }
}
