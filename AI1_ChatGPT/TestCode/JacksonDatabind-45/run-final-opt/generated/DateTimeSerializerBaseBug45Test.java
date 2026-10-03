package com.fasterxml.jackson.databind.ser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DateTimeSerializerBaseBug45Test
{
    private ObjectMapper utcMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        return mapper;
    }

    @Test
    public void testDefaultShapeDateArrayWithPatternUsesFormattedDateInsteadOfTimestamp() throws Exception {
        ObjectMapper mapper = utcMapper();
        DefaultShapeDateArray value = new DefaultShapeDateArray(new Date(0L));

        assertEquals("{\"date\":[\"1970-01-01\"]}", mapper.writeValueAsString(value));
    }

    @Test
    public void testDefaultShapeDatePropertyWithPatternUsesFormattedDateInsteadOfTimestamp() throws Exception {
        ObjectMapper mapper = utcMapper();
        DefaultShapeDate value = new DefaultShapeDate(new Date(0L));

        assertEquals("{\"date\":\"1970-01-01\"}", mapper.writeValueAsString(value));
    }

    @Test
    public void testDefaultShapePatternFormatsEveryDateArrayElement() throws Exception {
        ObjectMapper mapper = utcMapper();
        DefaultShapeDateArray value = new DefaultShapeDateArray(
                new Date[] { new Date(0L), new Date(24L * 60L * 60L * 1000L) });

        assertEquals("{\"date\":[\"1970-01-01\",\"1970-01-02\"]}",
                mapper.writeValueAsString(value));
    }

    @Test
    public void testExplicitNumericShapeOverridesDisabledTimestampFeature() throws Exception {
        ObjectMapper mapper = utcMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        assertEquals("{\"date\":0}",
                mapper.writeValueAsString(new NumericDate(new Date(0L))));
    }

    @Test
    public void testExplicitStringShapeUsesConfiguredPattern() throws Exception {
        ObjectMapper mapper = utcMapper();

        assertEquals("{\"date\":\"01/01/1970\"}",
                mapper.writeValueAsString(new StringDate(new Date(0L))));
    }

    @Test
    public void testDisabledTimestampFeatureProducesTextualDefaultDate() throws Exception {
        ObjectMapper mapper = utcMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonNode node = mapper.readTree(mapper.writeValueAsString(new PlainDate(new Date(0L))));

        assertTrue(node.get("date").isTextual());
        assertTrue(node.get("date").asText().startsWith("1970-01-01T00:00:00.000"));
    }

    public static class DefaultShapeDateArray {
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
        public Date[] date;

        public DefaultShapeDateArray() {
        }

        public DefaultShapeDateArray(Date... date) {
            this.date = date;
        }
    }

    public static class DefaultShapeDate {
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
        public Date date;

        public DefaultShapeDate() {
        }

        public DefaultShapeDate(Date date) {
            this.date = date;
        }
    }

    public static class NumericDate {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Date date;

        public NumericDate() {
        }

        public NumericDate(Date date) {
            this.date = date;
        }
    }

    public static class StringDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy", timezone = "UTC")
        public Date date;

        public StringDate() {
        }

        public StringDate(Date date) {
            this.date = date;
        }
    }

    public static class PlainDate {
        public Date date;

        public PlainDate() {
        }

        public PlainDate(Date date) {
            this.date = date;
        }
    }
}
