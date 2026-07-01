package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_returnOk_when_conversionRequestIsValid() throws Exception {
        UUID id = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        when(conversionService.convert(new ConversionRequest(id, BigDecimal.ONE, "metres", "feet")))
                .thenReturn(new ConversionResult(id, BigDecimal.ONE, "metres", "feet", new BigDecimal("3.28084")));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "ffffffff-ffff-ffff-ffff-ffffffffffff",
                                  "value": 1,
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                .andExpect(jsonPath("$.result").value(closeTo(3.28084, 0.00001)));
    }

    @Test
    void should_returnBadRequest_when_conversionValueIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "ffffffff-ffff-ffff-ffff-ffffffffffff",
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"));
    }

    @Test
    void should_returnBadRequest_when_conversionValueIsMalformed() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "ffffffff-ffff-ffff-ffff-ffffffffffff",
                                  "value": "abc",
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value"));
    }
}
