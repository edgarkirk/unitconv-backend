package com.edgarkirk.unitconv.api;

import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.ConversionValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ConversionController.class, UnitController.class})
@SuppressWarnings({"removal", "deprecation"})
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionService conversionService;

    @Test
    void should_return200AndConversionResult_when_validRequest() throws Exception {
        ConversionResultResponse response = new ConversionResultResponse(
                UUID.randomUUID(),
                new BigDecimal("10"),
                "metres",
                "feet",
                new BigDecimal("32.808400"));
        when(conversionService.convert(any())).thenReturn(response);

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(32.8084));
    }

    @Test
    void should_return400AndValidationError_when_unitsAreIncompatible() throws Exception {
        when(conversionService.convert(any())).thenThrow(new ConversionValidationException("Incompatible units: metres and gallons", null));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").value((String) null));
    }

    @Test
    void should_return400AndValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value must be numeric"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400AndValidationError_when_valueIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value is required"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return200AndAllUnits_when_listingUnits() throws Exception {
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
