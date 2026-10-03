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

    @Test
    public void parserAcceptsWhitespaceAroundCanonicalTokens()
    {
        TypeParser parser = new TypeParser(factory);

        JavaType type = parser.parse(
                "  java.util.Map < java.lang.String , java.util.List < java.lang.Long > >  ");

        assertEquals(Map.class, type.getRawClass());
        assertEquals(String.class, type.containedType(0).getRawClass());
        assertEquals(List.class, type.containedType(1).getRawClass());
        assertEquals(Long.class, type.containedType(1).containedType(0).getRawClass());
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
}
