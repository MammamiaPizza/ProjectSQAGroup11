package com.fasterxml.jackson.databind.deser.creators;

import java.beans.ConstructorProperties;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import static org.junit.Assert.assertEquals;

public class BasicDeserializerFactoryNamingStrategyRegressionTest
{
    @Test
    public void testSingleArgumentCreatorUsesSnakeCasePropertyName() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        OneProperty result = mapper.readValue("{\"param_name0\":\"snake-value\"}",
                OneProperty.class);

        assertEquals("snake-value", result.getParamName0());
    }

    @Test
    public void testSingleArgumentCreatorUsesDeclaredNameWithoutNamingStrategy() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        OneProperty result = mapper.readValue("{\"paramName0\":\"plain-value\"}",
                OneProperty.class);

        assertEquals("plain-value", result.getParamName0());
    }

    static class OneProperty {
        private final String paramName0;

        @JsonCreator
        @ConstructorProperties({ "paramName0" })
        public OneProperty(String paramName0) {
            this.paramName0 = paramName0;
        }

        public String getParamName0() {
            return paramName0;
        }
    }
}