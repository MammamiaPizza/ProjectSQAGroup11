package com.fasterxml.jackson.databind.introspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

public class JacksonAnnotationIntrospectorPrimitiveSerializationTypeTest
{
    public static class PublicFieldBean {
        @JsonSerialize(as = Integer.class)
        public int i;

        public PublicFieldBean(int value) {
            i = value;
        }
    }

    public static class GetterBean {
        private final int i;

        public GetterBean(int value) {
            i = value;
        }

        @JsonSerialize(as = Integer.class)
        public int getI() {
            return i;
        }
    }

    public static class IncompatibleTypeBean {
        @JsonSerialize(as = String.class)
        public int i = 1;
    }

    @Test
    public void serializesPrimitiveIntFieldAsItsBoxedSerializationType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"i\":13}", mapper.writeValueAsString(new PublicFieldBean(13)));
    }

    @Test
    public void serializesPrimitiveIntGetterAsItsBoxedSerializationType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"i\":0}", mapper.writeValueAsString(new GetterBean(0)));
    }

    @Test
    public void rejectsUnrelatedSerializationTypeForPrimitiveProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValueAsString(new IncompatibleTypeBean());
            fail("Expected incompatible serialization type to be rejected");
        } catch (InvalidDefinitionException e) {
            assertTrue(e.getMessage().contains("types not related"));
        }
    }
}