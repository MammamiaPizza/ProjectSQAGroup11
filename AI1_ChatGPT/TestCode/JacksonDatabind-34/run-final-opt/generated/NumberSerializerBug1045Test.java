package com.fasterxml.jackson.databind.ser.std;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NumberSerializerBug1045Test
{
    @Test
    public void bigDecimalVisitorReportsBigDecimalNumberType() throws Exception
    {
        NumberSerializer serializer = new NumberSerializer(BigDecimal.class);
        CapturingNumberVisitor visitor = new CapturingNumberVisitor();

        serializer.acceptJsonFormatVisitor(visitor, typeOf(BigDecimal.class));

        assertEquals(1, visitor.numberFormatRequests);
        assertEquals(JsonParser.NumberType.BIG_DECIMAL, visitor.numberType);
    }

    @Test
    public void bigIntegerVisitorReportsBigIntegerNumberType() throws Exception
    {
        NumberSerializer serializer = new NumberSerializer(BigInteger.class);
        CapturingNumberVisitor visitor = new CapturingNumberVisitor();

        serializer.acceptJsonFormatVisitor(visitor, typeOf(BigInteger.class));

        assertEquals(1, visitor.numberFormatRequests);
        assertEquals(JsonParser.NumberType.BIG_INTEGER, visitor.numberType);
    }

    @Test
    public void schemasDistinguishDecimalAndIntegerNumbers()
    {
        JsonNode decimalSchema = new NumberSerializer(BigDecimal.class).getSchema(null, null);
        JsonNode integerSchema = new NumberSerializer(BigInteger.class).getSchema(null, null);

        assertEquals("number", decimalSchema.get("type").asText());
        assertEquals("integer", integerSchema.get("type").asText());
    }

    @Test
    public void genericNumberUsesNumberFormatWithoutClaimingSpecificNumberType() throws Exception
    {
        CapturingNumberVisitor visitor = new CapturingNumberVisitor();

        NumberSerializer.instance.acceptJsonFormatVisitor(visitor, typeOf(Number.class));

        assertEquals(1, visitor.numberFormatRequests);
        assertNull(visitor.numberType);
        assertEquals("number", NumberSerializer.instance.getSchema(null, null).get("type").asText());
    }

    private static JavaType typeOf(Class<?> rawType)
    {
        return TypeFactory.defaultInstance().constructType(rawType);
    }

    private static class CapturingNumberVisitor extends JsonFormatVisitorWrapper.Base
    {
        int numberFormatRequests;
        JsonParser.NumberType numberType;

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type)
                throws JsonMappingException
        {
            ++numberFormatRequests;
            return new JsonNumberFormatVisitor.Base() {
                @Override
                public void numberType(JsonParser.NumberType type) {
                    numberType = type;
                }
            };
        }
    }
}
