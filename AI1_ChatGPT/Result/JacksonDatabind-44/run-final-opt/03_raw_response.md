import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;

public class SimpleTypeBug44Test
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type", defaultImpl = Default1125.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Named1125.class, name = "named")
    })
    public static class Base1125 {
        public int a;
    }

    public static class Default1125 extends Base1125 {
        public int b;
    }

    public static class Named1125 extends Base1125 {
        public String name;
    }

    @Test
    public void defaultSubtypeMustRetainItsOwnProperties() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Base1125 result = mapper.readValue("{\"a\":3,\"b\":7}", Base1125.class);

        assertTrue(result instanceof Default1125);
        assertEquals(3, result.a);
        assertEquals(7, ((Default1125) result).b);
    }

    @Test
    public void constructRejectsMapTypes() {
        try {
            SimpleType.construct(HashMap.class);
            fail("Map classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Map"));
        }
    }

    @Test
    public void constructRejectsCollectionTypes() {
        try {
            SimpleType.construct(ArrayList.class);
            fail("Collection classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Collection"));
        }
    }

    @Test
    public void constructRejectsArrayTypes() {
        try {
            SimpleType.construct(String[].class);
            fail("Array classes must not be constructible as SimpleType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("array"));
        }
    }

    @Test
    public void safeAndUnsafeScalarConstructionHaveConsistentIdentity() {
        SimpleType safe = SimpleType.construct(String.class);
        SimpleType unsafe = SimpleType.constructUnsafe(String.class);

        assertEquals(String.class, safe.getRawClass());
        assertEquals(String.class, unsafe.getRawClass());
        assertEquals(safe, unsafe);
        assertEquals(safe.hashCode(), unsafe.hashCode());
    }

    @Test
    public void scalarCanonicalNamesAndSignaturesAreCorrect() {
        SimpleType type = SimpleType.constructUnsafe(String.class);

        assertEquals("java.lang.String", type.toCanonical());
        assertEquals("[simple type, class java.lang.String]", type.toString());
        assertEquals("Ljava/lang/String;", type.getErasedSignature(new StringBuilder()).toString());
        assertEquals("Ljava/lang/String;", type.getGenericSignature(new StringBuilder()).toString());
        assertFalse(type.isContainerType());
    }

    @Test
    public void handlersAndStaticTypingProduceIndependentAdjustedTypes() {
        SimpleType original = SimpleType.constructUnsafe(Integer.class);
        Object valueHandler = new Object();
        Object typeHandler = new Object();

        SimpleType adjusted = original.withValueHandler(valueHandler)
                .withTypeHandler(typeHandler)
                .withStaticTyping();

        assertSame(valueHandler, adjusted.getValueHandler());
        assertSame(typeHandler, adjusted.getTypeHandler());
        assertTrue(adjusted.useStaticType());
        assertFalse(original.useStaticType());
        assertNull(original.getValueHandler());
        assertNull(original.getTypeHandler());
        assertSame(adjusted, adjusted.withValueHandler(valueHandler));
        assertSame(adjusted, adjusted.withTypeHandler(typeHandler));
        assertSame(adjusted, adjusted.withStaticTyping());
    }

    @Test
    public void simpleTypesRejectContentTypeOperations() {
        JavaType type = SimpleType.constructUnsafe(Integer.class);

        try {
            type.withContentType(SimpleType.constructUnsafe(String.class));
            fail("Simple types do not have content types");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }

        try {
            type.withContentTypeHandler(new Object());
            fail("Simple types do not have content type handlers");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }

        try {
            type.withContentValueHandler(new Object());
            fail("Simple types do not have content value handlers");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("no content types"));
        }
    }

    @Test
    public void narrowingCreatesTheRequestedSimpleRawType() {
        SimpleType base = SimpleType.constructUnsafe(Number.class);

        JavaType narrowed = SimpleType.constructUnsafe(Integer.class);

        assertEquals(Integer.class, narrowed.getRawClass());
        assertSame(base, base);
        assertEquals(Number.class, narrowed.getSuperClass().getRawClass());
    }

    @Test
    public void constructScalarBuildsSuperclassInformation() {
        SimpleType type = SimpleType.construct(Integer.class);

        assertNotNull(type.getSuperClass());
        assertEquals(Number.class, type.getSuperClass().getRawClass());
    }

    @Test
    public void constructUnsafeAllowsSimpleNonContainerRawTypes() {
        SimpleType type = SimpleType.constructUnsafe(Map.Entry.class);

        assertEquals(Map.Entry.class, type.getRawClass());
        assertEquals("java.util.Map$Entry", type.toCanonical());
    }
}