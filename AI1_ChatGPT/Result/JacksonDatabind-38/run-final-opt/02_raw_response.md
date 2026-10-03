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
}