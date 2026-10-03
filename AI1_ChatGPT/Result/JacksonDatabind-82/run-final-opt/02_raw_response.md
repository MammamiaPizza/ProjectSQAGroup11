package com.fasterxml.jackson.databind.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IgnoreGetterNotSetter1595Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void ignoredGetterMustNotPreventMatchingSetterDeserialization() throws Exception {
        GetterIgnoredSetterAvailable bean = mapper.readValue(
                "{\"name\":\"jack\"}", GetterIgnoredSetterAvailable.class);

        assertEquals("jack", bean.getStoredName());
    }

    @Test
    public void ignoredGetterOnlyPropertyShouldRemainIgnoredWhileOtherSetterWorks() throws Exception {
        GetterOnlyIgnored bean = mapper.readValue(
                "{\"ignored\":\"do-not-bind\",\"name\":\"jack\"}", GetterOnlyIgnored.class);

        assertNull(bean.getIgnoredValue());
        assertEquals("jack", bean.getName());
    }

    public static class GetterIgnoredSetterAvailable {
        private String name;

        @JsonIgnore
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStoredName() {
            return name;
        }
    }

    public static class GetterOnlyIgnored {
        private String ignored;
        private String name;

        @JsonIgnore
        public String getIgnored() {
            return ignored;
        }

        public String getIgnoredValue() {
            return ignored;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}