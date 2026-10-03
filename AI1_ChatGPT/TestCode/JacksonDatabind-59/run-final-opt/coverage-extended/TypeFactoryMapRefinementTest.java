import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TypeFactoryMapRefinementTest
{
    @Test
    public void specializedMapRetainsKeyAndValueTypes() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType base = factory.constructMapType(Map.class, CompoundKey.class, String.class);

        JavaType specialized = factory.constructSpecializedType(base, HashMap.class);

        assertEquals(HashMap.class, specialized.getRawClass());
        assertEquals(CompoundKey.class, specialized.getKeyType().getRawClass());
        assertEquals(String.class, specialized.getContentType().getRawClass());
    }

    @Test
    public void specializedMapUsesPreservedCompoundKeyTypeForDeserialization() throws Exception {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType base = factory.constructMapType(Map.class, CompoundKey.class, String.class);
        JavaType specialized = factory.constructSpecializedType(base, HashMap.class);

        Map<?, ?> result = new ObjectMapper().readValue(
                "{\"first:second\":\"value\"}", specialized);

        assertEquals(1, result.size());
        assertEquals("value", result.get(new CompoundKey("first:second")));
        assertTrue(result.keySet().iterator().next() instanceof CompoundKey);
    }

    @Test
    public void specializedMapRetainsHandlersAttachedToKeyAndContentTypes() {
        TypeFactory factory = TypeFactory.defaultInstance();
        Object keyHandler = new Object();
        Object contentHandler = new Object();

        JavaType keyType = factory.constructType(CompoundKey.class)
                .withValueHandler(keyHandler);
        JavaType contentType = factory.constructType(String.class)
                .withTypeHandler(contentHandler);
        JavaType base = MapLikeType.construct(Map.class, keyType, contentType);

        JavaType specialized = factory.constructSpecializedType(base, HashMap.class);

        assertSame(keyHandler, specialized.getKeyType().getValueHandler());
        assertSame(contentHandler, specialized.getContentType().getTypeHandler());
    }

    @Test
    public void changingMapKeyAndContentTypesKeepsBothIndependentTypeParameters() {
        TypeFactory factory = TypeFactory.defaultInstance();
        MapLikeType mapType = MapLikeType.construct(
                HashMap.class,
                factory.constructType(String.class),
                factory.constructType(Integer.class));

        JavaType changed = mapType.withKeyType(factory.constructType(Long.class))
                .withContentType(factory.constructType(Boolean.class));

        assertEquals(Long.class, changed.getKeyType().getRawClass());
        assertEquals(Boolean.class, changed.getContentType().getRawClass());
        assertEquals(HashMap.class, changed.getRawClass());
    }

    @Test
    public void specializedCollectionRetainsElementType() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType base = factory.constructCollectionType(Collection.class, CompoundKey.class);

        JavaType specialized = factory.constructSpecializedType(base, ArrayList.class);

        assertEquals(ArrayList.class, specialized.getRawClass());
        assertEquals(CompoundKey.class, specialized.getContentType().getRawClass());
    }

    public static class CompoundKey {
        private final String value;

        public CompoundKey(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CompoundKey
                    && value.equals(((CompoundKey) other).value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

@org.junit.Test
public void generalizedMapTypeRetainsKeyAndContentBindings() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType base = factory.constructMapType(
            java.util.HashMap.class, CompoundKey.class, String.class);

    com.fasterxml.jackson.databind.JavaType generalized =
            factory.constructGeneralizedType(base, java.util.Map.class);

    org.junit.Assert.assertEquals(java.util.Map.class, generalized.getRawClass());
    org.junit.Assert.assertEquals(CompoundKey.class, generalized.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, generalized.getContentType().getRawClass());
}

@org.junit.Test
public void mapSuperTypeRetainsResolvedKeyAndContentBindings() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType mapType = factory.constructMapType(
            java.util.LinkedHashMap.class, CompoundKey.class, String.class);

    com.fasterxml.jackson.databind.JavaType superType =
            mapType.findSuperType(java.util.Map.class);

    org.junit.Assert.assertNotNull(superType);
    org.junit.Assert.assertEquals(java.util.Map.class, superType.getRawClass());
    org.junit.Assert.assertEquals(CompoundKey.class, superType.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, superType.getContentType().getRawClass());
}

@org.junit.Test
public void canonicalMapTypeResolvesBothTypeParameters() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("java.util.Map<java.lang.Integer,java.lang.String>");

    org.junit.Assert.assertEquals(java.util.Map.class, type.getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void specializedCollectionRetainsElementValueHandler() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    Object handler = new Object();
    com.fasterxml.jackson.databind.JavaType elementType =
            factory.constructType(String.class).withValueHandler(handler);
    com.fasterxml.jackson.databind.JavaType base =
            factory.constructCollectionLikeType(java.util.Collection.class, elementType);

    com.fasterxml.jackson.databind.JavaType specialized =
            factory.constructSpecializedType(base, java.util.ArrayList.class);

    org.junit.Assert.assertEquals(java.util.ArrayList.class, specialized.getRawClass());
    org.junit.Assert.assertSame(handler, specialized.getContentType().getValueHandler());
}
}
