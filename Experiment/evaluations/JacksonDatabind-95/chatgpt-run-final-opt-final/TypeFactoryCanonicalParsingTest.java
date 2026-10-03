package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JavaType;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TypeFactoryCanonicalParsingTest
{
    private final TypeFactory factory = TypeFactory.defaultInstance();

    @Test
    public void parsesSimpleCanonicalClassName()
    {
        JavaType type = factory.constructFromCanonical("java.lang.String");

        assertEquals(String.class, type.getRawClass());
        assertEquals("java.lang.String", type.toCanonical());
    }

    @Test
    public void parsesSingleGenericCanonicalName()
    {
        JavaType type = factory.constructFromCanonical(
                "java.util.List<java.lang.String>");

        assertEquals(List.class, type.getRawClass());
        assertEquals(1, type.containedTypeCount());
        assertEquals(String.class, type.containedType(0).getRawClass());
        assertEquals("java.util.List<java.lang.String>", type.toCanonical());
    }

    @Test
    public void parsesNestedCanonicalNameAndRetainsAllArguments()
    {
        JavaType type = factory.constructFromCanonical(
                "java.util.Map<java.lang.String,java.util.List<java.lang.Integer>>");

        assertEquals(Map.class, type.getRawClass());
        assertEquals(2, type.containedTypeCount());
        assertEquals(String.class, type.containedType(0).getRawClass());

        JavaType valueType = type.containedType(1);
        assertNotNull(valueType);
        assertEquals(List.class, valueType.getRawClass());
        assertEquals(Integer.class, valueType.containedType(0).getRawClass());
        assertEquals(
                "java.util.Map<java.lang.String,java.util.List<java.lang.Integer>>",
                type.toCanonical());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnresolvableCanonicalClass()
    {
        factory.constructFromCanonical("no.such.package.NoSuchType");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCanonicalNameWithMissingClosingBracket()
    {
        factory.constructFromCanonical("java.util.List<java.lang.String");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCanonicalNameWithEmptyGenericArgumentList()
    {
        factory.constructFromCanonical("java.util.List<>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsTrailingTokensAfterCompleteType()
    {
        factory.constructFromCanonical("java.lang.String java.lang.Integer");
    }

@org.junit.Test
public void constructsPrimitiveTypeFromCanonicalName() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("int");

    org.junit.Assert.assertEquals(Integer.TYPE, type.getRawClass());
    org.junit.Assert.assertEquals("int", type.toCanonical());
}

@org.junit.Test
public void constructsGenericInnerClassTypeFromCanonicalName() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical(
                            "java.util.Map$Entry<java.lang.String,java.lang.Integer>");

    org.junit.Assert.assertEquals(java.util.Map.Entry.class, type.getRawClass());
    org.junit.Assert.assertEquals(2, type.containedTypeCount());
    org.junit.Assert.assertEquals(String.class, type.containedType(0).getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.containedType(1).getRawClass());
}

@org.junit.Test
public void specializesGenericCollectionTypePreservingElementType() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType baseType =
            factory.constructCollectionType(java.util.List.class, String.class);

    com.fasterxml.jackson.databind.JavaType specialized =
            factory.constructSpecializedType(baseType, java.util.ArrayList.class);

    org.junit.Assert.assertEquals(java.util.ArrayList.class, specialized.getRawClass());
    org.junit.Assert.assertEquals(1, specialized.containedTypeCount());
    org.junit.Assert.assertEquals(String.class, specialized.containedType(0).getRawClass());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void rejectsCanonicalNameWithWrongNumberOfGenericArguments() {
    com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
            .constructFromCanonical("java.util.List<java.lang.String,java.lang.Integer>");
}
}
