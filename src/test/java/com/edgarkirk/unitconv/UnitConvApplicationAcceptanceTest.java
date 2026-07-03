package com.edgarkirk.unitconv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitConvApplicationAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @BeforeEach
    void set_up() {
        conversionResultRepository.deleteAll();
    }

    @Test
    void should_return200_and_persistConversion_when_validMetresToFeetConversion() throws Exception {
        String body = """
                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                """;

        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(UUID.fromString(json.get("id").asText())).isNotNull();
        assertThat(json.get("inputValue").decimalValue()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(json.get("sourceUnit").asText()).isEqualTo("metres");
        assertThat(json.get("targetUnit").asText()).isEqualTo("feet");
        assertThat(json.get("result").decimalValue()).isGreaterThan(new BigDecimal("10"));
        assertThat(conversionResultRepository.count()).isEqualTo(1L);
    }

    @Test
    void should_preserveValueWithinTolerance_when_roundTripMetresAndFeet() throws Exception {
        String firstResponse = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BigDecimal feetValue = objectMapper.readTree(firstResponse).get("result").decimalValue();

        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{" + "\"value\":%s,\"sourceUnit\":\"feet\",\"targetUnit\":\"metres\"}", feetValue)))
                .andExpect(status().isOk())
                .andReturn();

        BigDecimal metresValue = objectMapper.readTree(result.getResponse().getContentAsString()).get("result").decimalValue();
        assertThat(metresValue).isCloseTo(new BigDecimal("10"), within(new BigDecimal("0.000001")));
    }

    @Test
    void should_return400_and_validationError_when_unitsAreIncompatible() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("message").asText()).contains("Incompatible");
        assertThat(json.get("field").isNull()).isTrue();
    }

    @Test
    void should_return400_and_validationError_when_valueIsNonNumeric() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("message").asText()).containsIgnoringCase("numeric");
        assertThat(json.get("field").asText()).isEqualTo("value");
    }

    @Test
    void should_return400_and_validationError_when_requiredFieldMissing() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("id").asText()).isNotBlank();
        assertThat(json.get("message").asText()).contains("Missing required field");
    }

    @Test
    void should_return200_and_allSixSupportedUnits_when_getUnits() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode array = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(array).hasSize(6);
        assertThat(StreamSupport.stream(array.spliterator(), false)
                .map(node -> node.get("name").asText())
                .toList())
                .containsExactlyInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons");
        assertThat(StreamSupport.stream(array.spliterator(), false)
                .map(node -> node.get("system").asText())
                .collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("metric", "imperial");
        assertThat(StreamSupport.stream(array.spliterator(), false).toList())
                .allSatisfy(node -> {
                    Set<String> fields = new HashSet<>();
                    node.fieldNames().forEachRemaining(fields::add);
                    assertThat(fields).containsExactlyInAnyOrder("id", "name", "system");
                });
    }
}
