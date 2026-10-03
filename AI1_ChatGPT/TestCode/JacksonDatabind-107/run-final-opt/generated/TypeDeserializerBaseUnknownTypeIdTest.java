package com.fasterxml.jackson.databind.deser.filter;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TypeDeserializerBaseUnknownTypeIdTest
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = KnownContent.class, name = "known")
    })
    public static abstract class Content {
        public String name;
    }

    public static class KnownContent extends Content {
    }

    public static class GenericContent {
        public List<Content> innerObjects;
    }

    @Test
    public void unknownTypeHandledAsVoidProducesNullCollectionElement() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final int[] calls = new int[1];
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public JavaType handleUnknownTypeId(DeserializationContext ctxt,
                    JavaType baseType, String subTypeId, TypeIdResolver idResolver,
                    String failureMsg) throws IOException {
                ++calls[0];
                assertEquals(Content.class, baseType.getRawClass());
                assertEquals("unknown", subTypeId);
                return ctxt.constructType(Void.class);
            }
        });

        GenericContent result = mapper.readValue(
                "{\"innerObjects\":[{\"type\":\"known\",\"name\":\"first\"},"
                        + "{\"type\":\"unknown\",\"name\":\"ignored\"}]}",
                GenericContent.class);

        assertEquals(1, calls[0]);
        assertEquals(2, result.innerObjects.size());
        assertEquals(KnownContent.class, result.innerObjects.get(0).getClass());
        assertEquals("first", result.innerObjects.get(0).name);
        assertNull(result.innerObjects.get(1));
    }

    @Test
    public void unknownTypeHandlerCanResolveUnknownIdToConcreteType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public JavaType handleUnknownTypeId(DeserializationContext ctxt,
                    JavaType baseType, String subTypeId, TypeIdResolver idResolver,
                    String failureMsg) throws IOException {
                if ("legacy-known".equals(subTypeId)) {
                    return ctxt.constructType(KnownContent.class);
                }
                return null;
            }
        });

        GenericContent result = mapper.readValue(
                "{\"innerObjects\":[{\"type\":\"legacy-known\",\"name\":\"resolved\"}]}",
                GenericContent.class);

        assertEquals(1, result.innerObjects.size());
        assertEquals(KnownContent.class, result.innerObjects.get(0).getClass());
        assertEquals("resolved", result.innerObjects.get(0).name);
    }

    @Test
    public void unhandledUnknownTypeReportsCollectionPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue(
                    "{\"innerObjects\":[{\"type\":\"known\",\"name\":\"first\"},"
                            + "{\"type\":\"unknown\",\"name\":\"second\"}]}",
                    GenericContent.class);
            fail("Expected unknown type id to fail without a problem handler");
        } catch (JsonMappingException e) {
            assertEquals(2, e.getPath().size());
            assertEquals("innerObjects", e.getPath().get(0).getFieldName());
            assertEquals(1, e.getPath().get(1).getIndex());
        }
    }
}
