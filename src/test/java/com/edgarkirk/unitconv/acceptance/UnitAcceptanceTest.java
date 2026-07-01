package com.edgarkirk.unitconv.acceptance;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.UnitconvBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = UnitconvBackendApplication.class)
@AutoConfigureMockMvc
class UnitAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_returnSupportedUnits_when_requested() throws Exception {
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].system").exists())
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
