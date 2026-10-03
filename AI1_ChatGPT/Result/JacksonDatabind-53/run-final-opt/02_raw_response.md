package com.fasterxml.jackson.databind.type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TypeBindingsAndMapRefinementTest
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UniqueId.class, name = "unique")
    })
    public static abstract class HasUniqueId {
        public String id;
    }

    public static class UniqueId extends HasUniqueId {
        public UniqueId() { }
    }

    public static class MapHolder {
        public Map<String, HasUniqueId> values;
    }

    public static class IdMap extends HashMap<String, HasUniqueId> {
        private static final long serialVersionUID = 1L;
    }

    public static class GenericMap<T> extends HashMap<String, T> {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testMapValueTypeInfoRefinesAbstractValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MapHolder result = mapper.readValue(
                "{\"values\":{\"first\":{\"type\":\"unique\",\"id\":\"id-1\"}}}",
                MapHolder.class);

        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        HasUniqueId value = result.values.get("first");
        assertTrue(value instanceof UniqueId);
        assertEquals("id-1", value.id);
    }

    @Test
    public void testInheritedGenericMapRetainsAbstractContentTypeInfo() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        IdMap result = mapper.readValue(
                "{\"first\":{\"type\":\"unique\",\"id\":\"id-2\"}}", IdMap.class);

        assertEquals(1, result.size());
        HasUniqueId value = result.get("first");
        assertTrue(value instanceof UniqueId);
        assertEquals("id-2", value.id);
    }

    @Test
    public void testMapBindingsExposeKeyAndValueParameters() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType stringType = factory.constructType(String.class);
        JavaType integerType = factory.constructType(Integer.class);

        TypeBindings bindings = TypeBindings.create(Map.class,
                new JavaType[] { stringType, integerType });

        assertFalse(bindings.isEmpty());
        assertEquals(2, bindings.size());
        assertEquals("K", bindings.getBoundName(0));
        assertEquals("V", bindings.getBoundName(1));
        assertEquals(stringType, bindings.getBoundType(0));
        assertEquals(integerType, bindings.getBoundType(1));
        assertEquals(stringType, bindings.findBoundType("K"));
        assertEquals(integerType, bindings.findBoundType("V"));
        assertEquals(2, bindings.getTypeParameters().size());
    }

    @Test
    public void testEmptyBindingsForNonGenericClass() {
        TypeBindings bindings = TypeBindings.createIfNeeded(String.class, (JavaType[]) null);

        assertSame(TypeBindings.emptyBindings(), bindings);
        assertTrue(bindings.isEmpty());
        assertEquals(0, bindings.size());
        assertTrue(bindings.getTypeParameters().isEmpty());
    }

    @Test
    public void testWrongSingleBindingCountHasUsefulMessage() {
        JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);

        try {
            TypeBindings.create(Map.class, stringType);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Can not create TypeBindings for class java.util.Map with 1 type parameter: class expects 2",
                    e.getMessage());
        }
    }

    @Test
    public void testWrongMultipleBindingCountHasUsefulMessage() {
        TypeFactory factory = TypeFactory.defaultInstance();

        try {
            TypeBindings.create(Map.class, new JavaType[] {
                    factory.constructType(String.class),
                    factory.constructType(Integer.class),
                    factory.constructType(Long.class)
            });
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Can not create TypeBindings for class java.util.Map with 3 type parameters: class expects 2",
                    e.getMessage());
        }
    }

    @Test
    public void testInheritedGenericMapBindingsResolveThroughSupertype() {
        TypeFactory factory = TypeFactory.defaultInstance();

        JavaType childType = factory.constructType(new TypeReference<GenericMap<Integer>>() { });
        JavaType mapType = childType.findSuperType(Map.class);

        assertNotNull(mapType);
        assertEquals(Map.class, mapType.getRawClass());
        assertEquals(String.class, mapType.getKeyType().getRawClass());
        assertEquals(Integer.class, mapType.getContentType().getRawClass());
    }

    @Test
    public void testSpecializedMapPreservesKeyAndValueTypes() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType baseMap = factory.constructType(new TypeReference<Map<String, Integer>>() { });

        JavaType specialized = factory.constructSpecializedType(baseMap, LinkedHashMap.class);

        assertEquals(LinkedHashMap.class, specialized.getRawClass());
        assertEquals(String.class, specialized.getKeyType().getRawClass());
        assertEquals(Integer.class, specialized.getContentType().getRawClass());
    }
}