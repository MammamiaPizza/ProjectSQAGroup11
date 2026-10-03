package com.fasterxml.jackson.databind.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TypeFactoryPropertiesTest
{
    @Test
    public void constructTypeResolvesPropertiesAsStringMap() {
        JavaType type = TypeFactory.defaultInstance().constructType(Properties.class);

        assertSame(Properties.class, type.getRawClass());
        assertSame(String.class, type.getKeyType().getRawClass());
        assertSame(String.class, type.getContentType().getRawClass());
    }

    @Test
    public void findTypeParametersResolvesPropertiesMapParameters() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType[] parameters = factory.findTypeParameters(
                factory.constructType(Properties.class), Map.class);

        assertEquals(2, parameters.length);
        assertSame(String.class, parameters[0].getRawClass());
        assertSame(String.class, parameters[1].getRawClass());
    }

    @Test
    public void readNumericJsonValueIntoPropertiesAsString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Properties properties = mapper.readValue("{\"answer\":123}", Properties.class);

        assertEquals("123", properties.getProperty("answer"));
    }

    @Test
    public void convertMapWithNumericValueIntoPropertiesAsString() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> source = new LinkedHashMap<String, Object>();
        source.put("answer", Integer.valueOf(129));

        Properties properties = mapper.convertValue(source, Properties.class);

        assertEquals("129", properties.getProperty("answer"));
    }

@org.junit.Test
public void constructTypeResolvesGenericArrayWildcardAndTypeVariable() throws Exception {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();

    com.fasterxml.jackson.databind.JavaType arrayType = factory.constructType(
            getClass().getDeclaredMethod("typeFactoryGenericArraySource").getGenericReturnType());
    org.junit.Assert.assertTrue(arrayType.getRawClass().isArray());
    org.junit.Assert.assertSame(java.util.List.class, arrayType.getContentType().getRawClass());
    org.junit.Assert.assertSame(java.lang.String.class,
            arrayType.getContentType().getContentType().getRawClass());

    com.fasterxml.jackson.databind.JavaType wildcardType = factory.constructType(
            getClass().getDeclaredMethod("typeFactoryWildcardSource").getGenericReturnType());
    org.junit.Assert.assertSame(java.util.List.class, wildcardType.getRawClass());
    org.junit.Assert.assertSame(java.lang.Number.class, wildcardType.getContentType().getRawClass());

    com.fasterxml.jackson.databind.JavaType variableType = factory.constructType(
            getClass().getDeclaredMethod("typeFactoryVariableSource").getGenericReturnType());
    org.junit.Assert.assertSame(java.lang.Number.class, variableType.getRawClass());

    com.fasterxml.jackson.databind.JavaType stringType = factory.constructType(java.lang.String.class);
    org.junit.Assert.assertSame(stringType,
            factory.constructType((java.lang.reflect.Type) stringType));
}

@org.junit.Test
public void constructTypeUsesUnknownTypesForRawMapAndCollection() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();

    com.fasterxml.jackson.databind.JavaType mapType = factory.constructType(java.util.HashMap.class);
    org.junit.Assert.assertSame(java.util.HashMap.class, mapType.getRawClass());
    org.junit.Assert.assertSame(java.lang.Object.class, mapType.getKeyType().getRawClass());
    org.junit.Assert.assertSame(java.lang.Object.class, mapType.getContentType().getRawClass());

    com.fasterxml.jackson.databind.JavaType collectionType =
            factory.constructType(java.util.ArrayList.class);
    org.junit.Assert.assertSame(java.util.ArrayList.class, collectionType.getRawClass());
    org.junit.Assert.assertSame(java.lang.Object.class, collectionType.getContentType().getRawClass());
}

@org.junit.Test
public void constructTypeRejectsUnrecognizedReflectiveType() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    java.lang.reflect.Type unsupportedType = new java.lang.reflect.Type() { };

    try {
        factory.constructType(unsupportedType);
        org.junit.Assert.fail("Expected IllegalArgumentException for unsupported Type");
    } catch (java.lang.IllegalArgumentException e) {
        // expected
    }
}

private java.util.List<java.lang.String>[] typeFactoryGenericArraySource() {
    return null;
}

private java.util.List<? extends java.lang.Number> typeFactoryWildcardSource() {
    return null;
}

private <T extends java.lang.Number> T typeFactoryVariableSource() {
    return null;
}
}
