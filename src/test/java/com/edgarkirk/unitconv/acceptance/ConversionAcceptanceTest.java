package com.edgarkirk.unitconv.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.api.dto.response.ValidationErrorResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConversionAcceptanceTest {

    private static final BigDecimal METRE_TO_FEET = new BigDecimal("3.280839895013123");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_returnSavedConversion_when_validInput() {
        long before = conversionResultRepository.count();

        ConversionResultResponse response = restTemplate.exchange(convertUri(), HttpMethod.POST,
                new HttpEntity<>(new ConversionRequest(new BigDecimal("1"), "metres", "feet"), jsonHeaders()),
                ConversionResultResponse.class).getBody();

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.inputValue()).isEqualByComparingTo("1");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isCloseTo(METRE_TO_FEET, within(new BigDecimal("0.000000000000001")));
        assertThat(conversionResultRepository.count()).isEqualTo(before + 1);
        assertThat(conversionResultRepository.findById(response.id())).isPresent();
    }

    @Test
    void should_roundTripConversion_when_reverseConversion() {
        ConversionResultResponse toFeet = restTemplate.exchange(convertUri(), HttpMethod.POST,
                new HttpEntity<>(new ConversionRequest(new BigDecimal("1"), "metres", "feet"), jsonHeaders()),
                ConversionResultResponse.class).getBody();

        assertThat(toFeet).isNotNull();

        ConversionResultResponse backToMetres = restTemplate.exchange(convertUri(), HttpMethod.POST,
                new HttpEntity<>(new ConversionRequest(toFeet.result(), "feet", "metres"), jsonHeaders()),
                ConversionResultResponse.class).getBody();

        assertThat(backToMetres).isNotNull();
        assertThat(backToMetres.result()).isCloseTo(new BigDecimal("1"), within(new BigDecimal("0.000000000000001")));
    }

    @Test
    void should_returnBadRequest_when_incompatibleUnits() {
        ResponseEntity<ValidationErrorResponse> response = restTemplate.exchange(convertUri(), HttpMethod.POST,
                new HttpEntity<>(new ConversionRequest(new BigDecimal("1"), "metres", "gallons"), jsonHeaders()),
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Units metres and gallons are incompatible");
        assertThat(response.getBody().field()).isNull();
    }

    @Test
    void should_returnBadRequest_when_nonNumericValue() {
        ResponseEntity<ValidationErrorResponse> response = restTemplate.exchange(convertUri(), HttpMethod.POST,
                new HttpEntity<>("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}", jsonHeaders()),
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("value must be numeric");
        assertThat(response.getBody().field()).isEqualTo("value");
    }

    @Test
    void should_returnUnits_when_getUnits() {
        ResponseEntity<UnitResponse[]> response = restTemplate.getForEntity(unitsUri(), UnitResponse[].class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        List<UnitResponse> units = List.of(response.getBody());
        assertThat(units).extracting(UnitResponse::name)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
        assertThat(units).allSatisfy(unit -> {
            assertThat(unit.id()).isInstanceOf(UUID.class);
            assertThat(unit.system()).isIn("metric", "imperial");
        });
    }

    private URI convertUri() {
        return URI.create("http://localhost:" + port + "/api/convert");
    }

    private URI unitsUri() {
        return URI.create("http://localhost:" + port + "/api/units");
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
