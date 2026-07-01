package com.edgarkirk.unitconv.acceptance;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.UnitconvBackendApplication;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = UnitconvBackendApplication.class)
@AutoConfigureMockMvc
class ConversionAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_returnConvertedResult_when_validMetresToFeetConversion() throws Exception {
        String id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa").toString();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "%s",
                                  "value": 1,
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.inputValue").value(1))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(closeTo(3.28084, 0.00001)));
    }

    @Test
    void should_returnConvertedResult_when_reverseFeetToMetresConversion() throws Exception {
        String id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb").toString();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "%s",
                                  "value": 3.28084,
                                  "sourceUnit": "feet",
                                  "targetUnit": "metres"
                                }
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.result").value(closeTo(1.0, 0.00001)));
    }

    @Test
    void should_returnBadRequest_when_unitsAreIncompatible() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "cccccccc-cccc-cccc-cccc-cccccccccccc",
                                  "value": 1,
                                  "sourceUnit": "metres",
                                  "targetUnit": "gallons"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres to gallons"));
    }

    @Test
    void should_returnBadRequest_when_valueIsNotNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "dddddddd-dddd-dddd-dddd-dddddddddddd",
                                  "value": "abc",
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value"));
    }

    @Test
    void should_returnBadRequest_when_valueIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"));
    }
}
