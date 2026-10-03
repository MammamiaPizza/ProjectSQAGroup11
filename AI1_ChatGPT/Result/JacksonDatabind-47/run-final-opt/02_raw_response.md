package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AnnotationIntrospectorRefineSerializationTypeTest
{
    @Test
    public void incompatibleSerializationTypeReportsUnrelatedTypes() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValueAsString(new IncompatibleTypeBean());
            fail("Expected incompatible @JsonSerialize(as=...) types to fail");
        } catch (JsonMappingException e) {
            assertTrue("Expected unrelated-types diagnostic, got: " + e.getMessage(),
                    e.getMessage().contains("types not related"));
        }
    }

    @Test
    public void specializedAbstractSerializationTypeIsAccepted() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(mapper.writeValueAsString(new SpecializedTypeBean()));

        assertEquals(3, root.get("value").get("base").asInt());
        assertEquals("special", root.get("value").get("kind").asText());
    }

    public static class IncompatibleTypeBean {
        @JsonSerialize(as = String.class)
        public Long getValue() {
            return Long.valueOf(12L);
        }
    }

    public static class Bean1178Base {
        public int getBase() {
            return 3;
        }
    }

    public static abstract class Bean1178Abstract extends Bean1178Base {
        public abstract String getKind();
    }

    public static class Bean1178Impl extends Bean1178Abstract {
        @Override
        public String getKind() {
            return "special";
        }
    }

    public static class SpecializedTypeBean {
        @JsonSerialize(as = Bean1178Abstract.class)
        public Bean1178Base getValue() {
            return new Bean1178Impl();
        }
    }
}