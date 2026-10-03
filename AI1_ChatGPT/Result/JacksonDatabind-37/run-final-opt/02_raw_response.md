package com.fasterxml.jackson.databind.type;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SimpleTypeGeneratedTest
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    public static class JsonMapSchema {
        public String name;
        public Map<String, JsonMapSchema> properties;
    }

    public static class GenericValue<T> {
        public T value;
    }

    @Test
    public void constructUnsafeHasUsableEmptyBindingsAndCanonicalName() {
        SimpleType type = SimpleType.constructUnsafe(JsonMapSchema.class);

        assertEquals(JsonMapSchema.class, type.getRawClass());
        assertEquals(0, type.containedTypeCount());
        assertEquals(JsonMapSchema.class.getName(), type.toCanonical());
        assertFalse(type.isContainerType());
    }

    @Test
    public void objectIdPropertyDeserializationAcceptsNameProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonMapSchema schema = mapper.readValue(
                "{\"name\":\"root\",\"properties\":{\"child\":{\"name\":\"child\"}}}",
                JsonMapSchema.class);

        assertEquals("root", schema.name);
        assertNotNull(schema.properties);
        assertEquals("child", schema.properties.get("child").name);
    }

    @Test
    public void constructCreatesNonContainerSimpleTypeWithClassSignatures() {
        SimpleType type = SimpleType.construct(String.class);

        assertEquals(String.class, type.getRawClass());
        assertFalse(type.isContainerType());
        assertEquals("java.lang.String", type.toCanonical());
        assertEquals("Ljava/lang/String;", type.getErasedSignature(new StringBuilder()).toString());
        assertEquals("Ljava/lang/String;", type.getGenericSignature(new StringBuilder()).toString());
    }

    @Test
    public void constructRejectsContainerAndArrayClasses() {
        try {
            SimpleType.construct(HashMap.class);
            fail("Map classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Map"));
        }

        try {
            SimpleType.construct(java.util.ArrayList.class);
            fail("Collection classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Collection"));
        }

        try {
            SimpleType.construct(String[].class);
            fail("Array classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("array"));
        }
    }

    @Test
    public void handlersAndStaticTypingPreserveTypeMetadata() {
        SimpleType base = SimpleType.construct(String.class);
        Object valueHandler = new Object();
        Object typeHandler = new Object();

        SimpleType withValueHandler = base.withValueHandler(valueHandler);
        SimpleType withTypeHandler = withValueHandler.withTypeHandler(typeHandler);
        SimpleType staticType = withTypeHandler.withStaticTyping();

        assertSame(valueHandler, withValueHandler.getValueHandler());
        assertSame(typeHandler, withTypeHandler.getTypeHandler());
        assertTrue(staticType.useStaticType());
        assertSame(staticType, staticType.withStaticTyping());
        assertSame(withValueHandler, withValueHandler.withValueHandler(valueHandler));
        assertSame(withTypeHandler, withTypeHandler.withTypeHandler(typeHandler));
        assertEquals(base, staticType);
    }

    @Test
    public void simpleTypesRejectContentTypeOperations() {
        SimpleType type = SimpleType.construct(String.class);

        try {
            type.withContentType(SimpleType.construct(Integer.class));
            fail("Simple types have no content type");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }

        try {
            type.withContentTypeHandler(new Object());
            fail("Simple types have no content type handler");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }

        try {
            type.withContentValueHandler(new Object());
            fail("Simple types have no content value handler");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }
    }

    @Test
    public void parameterizedSimpleTypeIncludesBindingInCanonicalAndGenericSignature() {
        JavaType type = TypeFactory.defaultInstance()
                .constructParametricType(GenericValue.class, String.class);

        assertTrue(type instanceof SimpleType);
        assertEquals(GenericValue.class.getName() + "<java.lang.String>", type.toCanonical());
        assertEquals("L" + GenericValue.class.getName().replace('.', '/')
                + "<Ljava/lang/String;>;", type.getGenericSignature(new StringBuilder()).toString());
    }
}