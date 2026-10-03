package com.fasterxml.jackson.databind.jsontype;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Bug965BigDecimalExternalTypeTest
{
    public static class ExternalDecimalBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        public Object value;
    }

    @Test
    public void testDelayedExternalTypeIdPreservesLargeNegativeBigDecimalScale() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BigDecimal expected = new BigDecimal("-10000000000.0000000001");

        ExternalDecimalBean result = mapper.readValue(
                "{\"value\":-10000000000.0000000001,\"type\":\"java.math.BigDecimal\"}",
                ExternalDecimalBean.class);

        assertTrue(result.value instanceof BigDecimal);
        assertEquals(expected, result.value);
    }

    @Test
    public void testExternalTypeIdRoundTripPreservesBigDecimalValueAndScale() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExternalDecimalBean input = new ExternalDecimalBean();
        BigDecimal expected = new BigDecimal("-10000000000.0000000001");
        input.value = expected;

        String json = mapper.writeValueAsString(input);
        ExternalDecimalBean result = mapper.readValue(json, ExternalDecimalBean.class);

        assertTrue(result.value instanceof BigDecimal);
        assertEquals(expected, result.value);
    }

    @Test
    public void testExternalTypeIdBeforeValuePreservesPositiveDecimalScale() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BigDecimal expected = new BigDecimal("42.00");

        ExternalDecimalBean result = mapper.readValue(
                "{\"type\":\"java.math.BigDecimal\",\"value\":42.00}",
                ExternalDecimalBean.class);

        assertTrue(result.value instanceof BigDecimal);
        assertEquals(expected, result.value);
    }

    @Test(expected = JsonMappingException.class)
    public void testMissingExternalTypeIdIsRejected() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mapper.readValue("{\"value\":1.25}", ExternalDecimalBean.class);
    }
}