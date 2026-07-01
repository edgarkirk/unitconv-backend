package com.edgarkirk.unitconv.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.application.exception.ConversionValidationException;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.dto.response.Unit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ConversionController.class)
@Import(RestExceptionHandler.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnConvertedResult_when_validConversionRequestIsSubmitted() throws Exception {
        final UUID requestId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(conversionService.convert(any(ConversionRequest.class))).thenReturn(
                new ConversionResult(requestId, new BigDecimal("1"), "metres", "feet", new BigDecimal("3.280840")));

        final MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(requestId, "1", "metres", "feet"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId.toString()))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsOnlyKeys("id", "inputValue", "sourceUnit", "targetUnit", "result");
    }

    @Test
    void should_returnUnits_when_getUnitsIsCalled() throws Exception {
        when(conversionService.listUnits()).thenReturn(List.of(
                new Unit(UUID.fromString("11111111-1111-1111-1111-111111111111"), "metres", "metric"),
                new Unit(UUID.fromString("22222222-2222-2222-2222-222222222222"), "feet", "imperial")));

        final MvcResult result = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn();

        final List<Map<String, Object>> units = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(units).hasSize(2);
        units.forEach(unit -> assertThat(unit).containsOnlyKeys("id", "name", "system"));
    }

    @Test
    void should_returnBadRequest_when_requiredFieldIsMissingFromConversionRequest() throws Exception {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put("id", UUID.fromString("11111111-1111-1111-1111-111111111111"));
        request.put("value", new BigDecimal("1"));
        request.put("sourceUnit", "metres");

        final MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Field 'targetUnit' is required.");
        assertThat(body.get("field")).isEqualTo("targetUnit");
    }

    @Test
    void should_returnBadRequest_when_serviceRejectsIncompatibleUnits() throws Exception {
        when(conversionService.convert(any(ConversionRequest.class)))
                .thenThrow(new ConversionValidationException("Units 'metres' and 'gallons' are incompatible.", "sourceUnit,targetUnit"));

        final Map<String, Object> request = validRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "1",
                "metres",
                "gallons");

        final MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Units 'metres' and 'gallons' are incompatible.");
        assertThat(body.get("field")).isEqualTo("sourceUnit,targetUnit");
    }

    @Test
    void should_returnBadRequest_when_valueCannotBeParsedAsNumber() throws Exception {
        final Map<String, Object> request = validRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "abc",
                "metres",
                "feet");

        final MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Map<String, Object> body = responseBody(result);
        assertThat(body).containsKeys("id", "message", "field");
        assertThat(body.get("message").toString()).isEqualTo("Field 'value' must be numeric.");
        assertThat(body.get("field")).isEqualTo("value");
    }

    private Map<String, Object> validRequest(final UUID id, final Object value, final String sourceUnit, final String targetUnit) {
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
}
