package com.fasterxml.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JacksonAnnotationIntrospectorUnwrappedTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void unwrappedPrivateFieldIsDiscoveredAsSerializableProperty() throws Exception
    {
        String json = mapper.writeValueAsString(new DefaultOuter("value"));

        assertEquals("{\"value\":\"value\"}", json);
    }

    @Test
    public void unwrappedPropertyAppliesPrefixAndSuffixToContainedNames() throws Exception
    {
        String json = mapper.writeValueAsString(new PrefixedOuter("value"));

        assertEquals("{\"before_value_after\":\"value\"}", json);
    }

    @Test
    public void disabledUnwrappedAnnotationKeepsPropertyWrapped() throws Exception
    {
        String json = mapper.writeValueAsString(new DisabledOuter("value"));

        assertEquals("{\"nested\":{\"value\":\"value\"}}", json);
    }

    public static class Value
    {
        public String value;

        public Value() {
        }

        public Value(String value) {
            this.value = value;
        }
    }

    public static class DefaultOuter
    {
        @JsonUnwrapped
        private Value nested;

        public DefaultOuter() {
        }

        public DefaultOuter(String value) {
            nested = new Value(value);
        }
    }

    public static class PrefixedOuter
    {
        @JsonUnwrapped(prefix = "before_", suffix = "_after")
        private Value nested;

        public PrefixedOuter() {
        }

        public PrefixedOuter(String value) {
            nested = new Value(value);
        }
    }

    public static class DisabledOuter
    {
        @JsonUnwrapped(enabled = false)
        private Value nested;

        public DisabledOuter() {
        }

        public DisabledOuter(String value) {
            nested = new Value(value);
        }
    }
}