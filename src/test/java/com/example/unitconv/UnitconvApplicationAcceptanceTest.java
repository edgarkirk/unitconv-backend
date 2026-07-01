package com.example.unitconv;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = UnitconvApplication.class)
@AutoConfigureMockMvc
class UnitconvApplicationAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_returnConvertedResult_when_metresToFeet() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"%s","value":1,"sourceUnit":"metres","targetUnit":"feet"}
                        """.formatted(id)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.inputValue").value(1))
            .andExpect(jsonPath("$.sourceUnit").value("metres"))
            .andExpect(jsonPath("$.targetUnit").value("feet"))
            .andExpect(jsonPath("$.result").value(closeTo(3.28084, 0.00001)));
    }

    @Test
    void should_returnConvertedResult_when_kilometresToMiles() throws Exception {
        UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");

        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"%s","value":1,"sourceUnit":"kilometres","targetUnit":"miles"}
                        """.formatted(id)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.inputValue").value(1))
            .andExpect(jsonPath("$.sourceUnit").value("kilometres"))
            .andExpect(jsonPath("$.targetUnit").value("miles"))
            .andExpect(jsonPath("$.result").isNumber());
    }

    @Test
    void should_returnApproximatelyOriginalValue_when_reverseConversionFeetToMetres() throws Exception {
        UUID id = UUID.fromString("33333333-3333-3333-3333-333333333333");

        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"%s","value":3.28084,"sourceUnit":"feet","targetUnit":"metres"}
                        """.formatted(id)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.result").value(closeTo(1.0, 0.00001)));
    }

    @Test
    void should_returnBadRequest_when_incompatibleUnits() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"44444444-4444-4444-4444-444444444444","value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons cannot be converted"));
    }

    @Test
    void should_returnBadRequest_when_nonNumericValue() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"55555555-5555-5555-5555-555555555555","value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid input: value must be numeric"));
    }

    @Test
    void should_returnBadRequest_when_missingId() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"value":1,"sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Missing required field: id"));
    }

    @Test
    void should_returnBadRequest_when_missingValue() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"66666666-6666-6666-6666-666666666666","sourceUnit":"metres","targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Missing required field: value"));
    }

    @Test
    void should_returnBadRequest_when_missingSourceUnit() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"77777777-7777-7777-7777-777777777777","value":1,"targetUnit":"feet"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"));
    }

    @Test
    void should_returnBadRequest_when_missingTargetUnit() throws Exception {
        mockMvc.perform(post("/api/convert")
                .contentType(APPLICATION_JSON)
                .content("""
                        {"id":"88888888-8888-8888-8888-888888888888","value":1,"sourceUnit":"metres"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"));
    }

    @Test
    void should_returnSupportedUnits_when_listingUnits() throws Exception {
        mockMvc.perform(get("/api/units"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[0].id", notNullValue()))
            .andExpect(jsonPath("$[0].name").value("metres"))
            .andExpect(jsonPath("$[0].system").value("metric"))
            .andExpect(jsonPath("$[1].name").value("feet"))
            .andExpect(jsonPath("$[1].system").value("imperial"))
            .andExpect(jsonPath("$[2].name").value("kilometres"))
            .andExpect(jsonPath("$[2].system").value("metric"))
            .andExpect(jsonPath("$[3].name").value("miles"))
            .andExpect(jsonPath("$[3].system").value("imperial"))
            .andExpect(jsonPath("$[4].name").value("litres"))
            .andExpect(jsonPath("$[4].system").value("metric"))
            .andExpect(jsonPath("$[5].name").value("gallons"))
            .andExpect(jsonPath("$[5].system").value("imperial"));
    }
}
