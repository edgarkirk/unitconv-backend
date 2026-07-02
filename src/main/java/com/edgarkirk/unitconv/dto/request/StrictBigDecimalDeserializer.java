package com.edgarkirk.unitconv.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.math.BigDecimal;

public class StrictBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken currentToken = parser.currentToken();
        if (currentToken != null && currentToken.isNumeric()) {
            return parser.getDecimalValue();
        }

        throw InvalidFormatException.from(parser,
                "Invalid value: expected a numeric JSON number",
                parser.getText(),
                BigDecimal.class);
    }
}
