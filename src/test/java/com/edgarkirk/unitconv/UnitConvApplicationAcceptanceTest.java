package com.edgarkirk.unitconv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.persistence.ConversionResultRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class UnitConvApplicationAcceptanceTest {

    private static final UUID REQUEST_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @BeforeEach
    void setUp() {
        conversionResultRepository.deleteAll();
    }

    @Test
    void should_returnConversionResult_and_persistIt_when_validMetresToFeetConversionIsSubmitted() throws Exception {
        final MvcResult result = performConvert(validRequest(REQUEST_ID, new BigDecimal("1"), "metres", "feet"))
                .andExpect(status().isOk())
                .andReturn();

        final Map<String, Object> body = responseBody(result);

        assertThat(body).containsOnlyKeys("id", "inputValue", "sourceUnit", "targetUnit", "result");
        assertThat(body.get("id").toString()).isEqualTo(REQUEST_ID.toString());
        assertThat(decimal(body.get("inputValue"))).isEqualByComparingTo("1");
        assertThat(body.get("sourceUnit")).isEqualTo("metres");
        assertThat(body.get("targetUnit")).isEqualTo("feet");
        assertThat(decimal(body.get("result"))).isEqualByComparingTo("3.280840");

        assertThat(conversionResultRepository.findAll()).hasSize(1);
        final var persisted = conversionResultRepository.findAll().get(0);
        assertThat(persisted.getInputValue()).isEqualByComparingTo("1");
        assertThat(persisted.getSourceUnit()).isEqualTo("metres");
        assertThat(persisted.getTargetUnit()).isEqualTo("feet");
        assertThat(persisted.getResult()).isEqualByComparingTo("3.280840");
    }

    @Test
    void should_persistExactlyOneConversionResult_when_validConversionSucceeds() throws Exception {
        performConvert(validRequest(REQUEST_ID, new BigDecimal("2.5"), "kilometres", "miles"))
                .andExpect(status().isOk());

        assertThat(conversionResultRepository.findAll()).hasSize(1);
    }

    @Test
    void should_returnOriginalValueWithinPrecision_when_metresToFeetThenFeetToMetres() throws Exception {
        final MvcResult firstResult = performConvert(validRequest(REQUEST_ID, new BigDecimal("12.5"), "metres", "feet"))
                .andExpect(status().isOk())
                .andReturn();

        final BigDecimal feetValue = decimal(responseBody(firstResult).get("result"));
        final UUID reverseRequestId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        final MvcResult secondResult = performConvert(validRequest(reverseRequestId, feetValue, "feet", "metres"))
                .andExpect(status().isOk())
                .andReturn();

        final BigDecimal roundTripped = decimal(responseBody(secondResult).get("result"));
        assertThat(roundTripped.subtract(new BigDecimal("12.5")).abs())
                .isLessThan(new BigDecimal("0.00001"));
    }

    @Test
    void should_returnBadRequest_when_metresToGallonsConversionIsSubmitted() throws Exception {
        final MvcResult result = performConvert(validRequest(REQUEST_ID, new BigDecimal("1"), "metres", "gallons"))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Units 'metres' and 'gallons' are incompatible.");
        assertThat(body.get("field")).isEqualTo("sourceUnit,targetUnit");
    }

    @Test
    void should_returnBadRequest_when_valueIsNonNumeric() throws Exception {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put("id", REQUEST_ID);
        request.put("value", "abc");
        request.put("sourceUnit", "metres");
        request.put("targetUnit", "feet");

        final MvcResult result = performConvert(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Field 'value' must be numeric.");
        assertThat(body.get("field")).isEqualTo("value");
    }

    @Test
    void should_returnBadRequest_when_requiredIdIsMissing() throws Exception {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put("value", new BigDecimal("1"));
        request.put("sourceUnit", "metres");
        request.put("targetUnit", "feet");

        final MvcResult result = performConvert(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Field 'id' is required.");
        assertThat(body.get("field")).isEqualTo("id");
    }

    @Test
    void should_returnSupportedUnits_when_getUnitsIsCalled() throws Exception {
        final MvcResult result = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        final List<Map<String, Object>> units = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(units).hasSize(6);
    }

    @Test
    void should_returnUnitsWithIdNameAndSystem_when_getUnitsIsCalled() throws Exception {
        final MvcResult result = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        final List<Map<String, Object>> units = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        units.forEach(unit -> assertThat(unit).containsOnlyKeys("id", "name", "system"));
    }

    @Test
    void should_returnAllSixSupportedUnits_when_getUnitsIsCalled() throws Exception {
        final MvcResult result = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        final List<Map<String, Object>> units = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        final Set<String> names = units.stream()
                .map(unit -> unit.get("name").toString())
                .collect(Collectors.toSet());

        assertThat(names).containsExactlyInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons");
    }

    @Test
    void should_returnValidationErrorBody_when_anyValidationFailureOccurs() throws Exception {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put("id", REQUEST_ID);
        request.put("value", new BigDecimal("1"));
        request.put("sourceUnit", "metres");
        request.put("targetUnit", "gallons");

        final MvcResult result = performConvert(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("id").toString()).isNotBlank();
        assertThat(body.get("message").toString()).isNotBlank();
    }

    @Test
    void should_return200_when_conversionSucceeds() throws Exception {
        performConvert(validRequest(REQUEST_ID, new BigDecimal("7"), "litres", "gallons"))
                .andExpect(status().isOk());
    }

    @Test
    void should_matchOpenApiResponseShapes_when_convertAndUnitsEndpointsAreCalled() throws Exception {
        final MvcResult convertResult = performConvert(validRequest(REQUEST_ID, new BigDecimal("1"), "metres", "feet"))
                .andExpect(status().isOk())
                .andReturn();

        final MvcResult unitsResult = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        final Map<String, Object> convertBody = responseBody(convertResult);
        final List<Map<String, Object>> units = objectMapper.readValue(
                unitsResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(convertBody).containsOnlyKeys("id", "inputValue", "sourceUnit", "targetUnit", "result");
        assertThat(units).isNotEmpty();
        units.forEach(unit -> assertThat(unit).containsOnlyKeys("id", "name", "system"));
    }

    private org.springframework.test.web.servlet.ResultActions performConvert(final Map<String, Object> request) throws Exception {
        return mockMvc.perform(post("/api/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private Map<String, Object> validRequest(final UUID id, final BigDecimal value, final String sourceUnit, final String targetUnit) {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put("id", id);
        request.put("value", value);
        request.put("sourceUnit", sourceUnit);
        request.put("targetUnit", targetUnit);
        return request;
    }

    private Map<String, Object> responseBody(final MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    private BigDecimal decimal(final Object value) {
        return new BigDecimal(value.toString());
    }
}
