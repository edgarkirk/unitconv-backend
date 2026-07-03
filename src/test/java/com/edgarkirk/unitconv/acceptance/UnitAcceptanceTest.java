package com.edgarkirk.unitconv.acceptance;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return200AndAllSupportedUnits_when_getUnits() throws Exception {
        mockMvc.perform(get("/api/units").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value(notNullValue()));
    }

    @Test
    void should_returnExactlySixRecords_when_getUnits() throws Exception {
        mockMvc.perform(get("/api/units").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    void should_returnIdNameAndSystemFields_when_getUnits() throws Exception {
        mockMvc.perform(get("/api/units").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value(notNullValue()))
                .andExpect(jsonPath("$[0].system").value(notNullValue()));
    }

    @Test
    void should_notRequireAuthenticationHeaders_when_accessingPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/units").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void should_rejectBatchPayload_when_singleValueEndpointReceivesArray() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(notNullValue()));
    }
}
