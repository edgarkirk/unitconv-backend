package com.edgarkirk.unitconv.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitApiAcceptanceTest {

    private static final String UNITS_PATH = "/api/units";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return200_andAllSupportedUnits_when_listingUnits() throws Exception {
        String response = mockMvc.perform(get(UNITS_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertThat(json.isArray()).isTrue();
        assertThat(json).hasSize(6);
    }

    @Test
    void should_returnExactlyIdNameAndSystemFields_when_listingUnits() throws Exception {
        String response = mockMvc.perform(get(UNITS_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        for (JsonNode unit : json) {
            assertThat(fieldNames(unit)).containsExactlyInAnyOrder("id", "name", "system");
        }
    }

    private static java.util.Set<String> fieldNames(JsonNode node) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fieldNames(), 0), false)
                .collect(Collectors.toSet());
    }

    @Test
    void should_returnTheSixPredefinedUnitNamesOnly_when_listingUnits() throws Exception {
        String response = mockMvc.perform(get(UNITS_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        List<String> unitNames = json.findValuesAsText("name");
        assertThat(unitNames).containsExactlyInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons");
        assertThat(unitNames).hasSize(6);
    }
}
