package com.fasterxml.jackson.databind.ser.std;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DateTimeSerializerBaseConfigOverrideTest
{
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final java.sql.Date SQL_DATE = new java.sql.Date(324547200000L);

    @Test
    public void rootSqlDateUsesConfiguredPatternOverride() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(UTC);
        mapper.configOverride(java.sql.Date.class)
                .setFormat(JsonFormat.Value.forPattern("yyyy+MM+dd"));

        assertEquals("\"1980+04+14\"", mapper.writeValueAsString(SQL_DATE));
    }

    @Test
    public void rootSqlDateStringShapeOverrideUsesConfiguredDateFormat() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        format.setTimeZone(UTC);
        mapper.setDateFormat(format);
        mapper.configOverride(java.sql.Date.class)
                .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));

        assertEquals("\"1980/04/14\"", mapper.writeValueAsString(SQL_DATE));
    }

    @Test
    public void rootSqlDateNumericShapeOverrideWinsWhenGlobalTimestampsAreDisabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configOverride(java.sql.Date.class)
                .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER));

        assertEquals("324547200000", mapper.writeValueAsString(SQL_DATE));
    }

    @Test
    public void propertyPatternOverrideStillSerializesSqlDateAsString() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"date\":\"14/04/1980\"}",
                mapper.writeValueAsString(new PatternHolder(SQL_DATE)));
    }

    @Test
    public void epochSqlDateIsNotEmptyWhenNonEmptyInclusionIsUsed() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        assertEquals("{\"date\":0}",
                mapper.writeValueAsString(new NonEmptyHolder(new java.sql.Date(0L))));
    }

    public static class PatternHolder {
        @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
        public java.sql.Date date;

        public PatternHolder(java.sql.Date date) {
            this.date = date;
        }
    }

    public static class NonEmptyHolder {
        public java.sql.Date date;

        public NonEmptyHolder(java.sql.Date date) {
            this.date = date;
        }
    }
}