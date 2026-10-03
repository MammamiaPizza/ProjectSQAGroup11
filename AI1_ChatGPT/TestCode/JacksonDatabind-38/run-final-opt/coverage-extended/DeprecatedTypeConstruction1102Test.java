package com.fasterxml.jackson.databind.type;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DeprecatedTypeConstruction1102Test
{
    public static class Point {
        public int x;
    }

    public static class Point3D extends Point {
        public int z;
    }

    @Test
    public void explicitDeprecatedMapTypeDeserializesDeclaredValueType() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JavaType pointType = SimpleType.construct(Point.class);
        MapType mapType = MapType.construct(Map.class,
                SimpleType.construct(String.class), pointType);

        Map<?, ?> result = mapper.readValue("{\"point\":{\"x\":13}}", mapType);

        assertEquals(Point.class, result.get("point").getClass());
        assertEquals(13, ((Point) result.get("point")).x);
    }

    @Test
    public void explicitDeprecatedCollectionTypeDeserializesDeclaredElementType() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JavaType pointType = SimpleType.construct(Point.class);
        CollectionType collectionType = CollectionType.construct(Collection.class, pointType);

        Collection<?> result = mapper.readValue("[{\"x\":27}]", collectionType);

        assertEquals(1, result.size());
        Object value = result.iterator().next();
        assertEquals(Point.class, value.getClass());
        assertEquals(27, ((Point) value).x);
    }

    @Test
    public void deprecatedSimpleTypeRetainsInheritedPropertiesForSubtypeDeserialization() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleType point3DType = SimpleType.construct(Point3D.class);

        Point3D result = mapper.readValue("{\"x\":4,\"z\":9}", point3DType);

        assertEquals(4, result.x);
        assertEquals(9, result.z);
    }

    @Test
    public void deprecatedSimpleTypeConstructsUsableOrdinaryPojoType() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleType pointType = SimpleType.construct(Point.class);

        Point result = mapper.readValue("{\"x\":101}", pointType);

        assertEquals(Point.class, pointType.getRawClass());
        assertEquals(101, result.x);
    }

    @Test
    public void deprecatedSimpleTypeRejectsMapClasses()
    {
        assertSimpleTypeRejected(Map.class);
    }

    @Test
    public void deprecatedSimpleTypeRejectsCollectionAndArrayClasses()
    {
        assertSimpleTypeRejected(Collection.class);
        assertSimpleTypeRejected(Point[].class);
    }

    private void assertSimpleTypeRejected(Class<?> type)
    {
        try {
            SimpleType.construct(type);
            fail("Expected SimpleType.construct to reject " + type.getName());
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

@org.junit.Test
public void deprecatedCollectionTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JavaType stringType = mapper.constructType(String.class);
    com.fasterxml.jackson.databind.JavaType integerType = mapper.constructType(Integer.class);
    com.fasterxml.jackson.databind.type.CollectionType type =
            com.fasterxml.jackson.databind.type.CollectionType.construct(java.util.ArrayList.class, stringType);

    org.junit.Assert.assertSame(type, type.withContentType(stringType));
    org.junit.Assert.assertNotSame(type, type.withContentType(integerType));
    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}

@org.junit.Test
public void deprecatedMapTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JavaType stringType = mapper.constructType(String.class);
    com.fasterxml.jackson.databind.JavaType integerType = mapper.constructType(Integer.class);
    com.fasterxml.jackson.databind.type.MapType type =
            com.fasterxml.jackson.databind.type.MapType.construct(java.util.LinkedHashMap.class,
                    stringType, integerType);

    org.junit.Assert.assertSame(type, type.withContentType(integerType));
    org.junit.Assert.assertSame(type, type.withKeyType(stringType));
    org.junit.Assert.assertNotSame(type, type.withContentType(stringType));
    org.junit.Assert.assertNotSame(type, type.withKeyType(integerType));
    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withKeyTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withKeyValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}

@org.junit.Test
public void deprecatedSimpleTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.type.SimpleType type =
            com.fasterxml.jackson.databind.type.SimpleType.construct(String.class);

    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}
}
