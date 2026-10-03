package com.fasterxml.jackson.databind.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InnerClassPropertyBug72Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreatorBoundInnerPropertyMayBeNull() throws Exception {
        CreatorOuter result = mapper.readValue("{\"a\":null}", CreatorOuter.class);

        assertNotNull(result);
        assertNull(result.a);
    }

    @Test
    public void testNonStaticInnerPropertyIsConstructedAndPopulated() throws Exception {
        RegularOuter result = mapper.readValue(
                "{\"value\":{\"number\":13,\"text\":\"inner\"}}", RegularOuter.class);

        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(13, result.value.number);
        assertEquals("inner", result.value.text);
    }

    @Test
    public void testNamedNonStaticInnerPropertyIsAssignedUsingJsonName() throws Exception {
        RenamedOuter result = mapper.readValue(
                "{\"renamed\":{\"number\":7}}", RenamedOuter.class);

        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(7, result.value.number);
    }

    public static class CreatorOuter {
        public final Inner a;

        @JsonCreator
        public CreatorOuter(@JsonProperty("a") Inner a) {
            this.a = a;
        }

        public class Inner {
            public int number;
        }
    }

    public static class RegularOuter {
        public Inner value;

        public class Inner {
            public int number;
            public String text;
        }
    }

    public static class RenamedOuter {
        @JsonProperty("renamed")
        public Inner value;

        public class Inner {
            public int number;
        }
    }
}