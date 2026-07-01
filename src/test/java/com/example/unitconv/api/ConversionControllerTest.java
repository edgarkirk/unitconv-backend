package com.example.unitconv.api;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.unitconv.api.controller.ConversionController;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.application.ConversionService;
import com.example.unitconv.application.exception.IncompatibleUnitsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnConvertedResult_when_validRequest() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        given(conversionService.convert(org.mockito.ArgumentMatchers.any()))
            .willReturn(new ConversionResult(id, BigDecimal.ONE, "metres", "feet", new BigDecimal("3.28084")));

        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","value":1,"sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.inputValue").value(1))
            .andExpect(jsonPath("$.sourceUnit").value("metres"))
            .andExpect(jsonPath("$.targetUnit").value("feet"))
            .andExpect(jsonPath("$.result").value(closeTo(3.28084, 0.00001)));
    }

    @Test
    void should_returnBadRequest_when_serviceRejectsIncompatibleUnits() throws Exception {
        doThrow(new IncompatibleUnitsException("Incompatible units: metres and gallons cannot be converted"))
            .when(conversionService).convert(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb","value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons cannot be converted"));
    }

    @Test
    void should_returnBadRequest_when_valueMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"cccccccc-cccc-cccc-cccc-cccccccccccc","sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Missing required field: value"));
    }

    @Test
    void should_returnBadRequest_when_valueIsNotNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"dddddddd-dddd-dddd-dddd-dddddddddddd","value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid input: value must be numeric"));
    }

}
