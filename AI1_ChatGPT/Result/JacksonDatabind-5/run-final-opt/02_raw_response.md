package com.fasterxml.jackson.databind.introspect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestAnnotatedClassMixIn515
{
    public static class DirectPerson {
        public String value() {
            return "direct";
        }
    }

    public static class PersonBase {
        public String value() {
            return "inherited";
        }
    }

    public static class PersonImpl extends PersonBase {
    }

    public abstract static class PersonMixIn {
        @JsonProperty("name")
        public abstract String value();
    }

    @Test
    public void testMixInExposesDirectNonBeanMethod() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixInAnnotations(DirectPerson.class, PersonMixIn.class);

        assertEquals("{\"name\":\"direct\"}", mapper.writeValueAsString(new DirectPerson()));
    }

    @Test
    public void testMixInOnSubclassExposesInheritedNonBeanMethod() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixInAnnotations(PersonImpl.class, PersonMixIn.class);

        assertEquals("{\"name\":\"inherited\"}", mapper.writeValueAsString(new PersonImpl()));
    }
}