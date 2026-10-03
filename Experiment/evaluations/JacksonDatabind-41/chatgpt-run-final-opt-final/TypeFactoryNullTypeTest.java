package com.fasterxml.jackson.databind.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypeFactoryNullTypeTest
{
    @Test(expected = IllegalArgumentException.class)
    public void constructTypeWithNullUsesUnknownType()
    {
        JavaType type = TypeFactory.defaultInstance().constructType((Type) null);

        assertNotNull(type);
        assertEquals(Object.class, type.getRawClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parameterizedTypeWithNullArgumentUsesUnknownContentType()
    {
        JavaType type = TypeFactory.defaultInstance().constructType(
                new NullArgumentParameterizedType(List.class));

        assertEquals(List.class, type.getRawClass());
        assertNotNull(type.getContentType());
        assertEquals(Object.class, type.getContentType().getRawClass());
    }

    @Test
    public void normalParameterizedTypeRetainsContentType()
    {
        Type source = new TypeReference<List<String>>() { }.getType();

        JavaType type = TypeFactory.defaultInstance().constructType(source);

        assertEquals(List.class, type.getRawClass());
        assertEquals(String.class, type.getContentType().getRawClass());
    }

    @Test
    public void classArrayTypeResolvesComponentType()
    {
        JavaType type = TypeFactory.defaultInstance().constructType(String[].class);

        assertTrue(type.isArrayType());
        assertEquals(String[].class, type.getRawClass());
        assertEquals(String.class, type.getContentType().getRawClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedNonNullTypeIsRejected()
    {
        TypeFactory.defaultInstance().constructType(new Type() { });
    }

    private static final class NullArgumentParameterizedType implements ParameterizedType
    {
        private final Class<?> rawType;

        NullArgumentParameterizedType(Class<?> rawType)
        {
            this.rawType = rawType;
        }

        @Override
        public Type[] getActualTypeArguments()
        {
            return new Type[] { null };
        }

        @Override
        public Type getRawType()
        {
            return rawType;
        }

        @Override
        public Type getOwnerType()
        {
            return null;
        }
    }

@org.junit.Test
public void constructFromCanonicalResolvesPrimitiveNames() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    String[] names = { "int", "long", "double", "boolean", "byte", "char", "short", "void" };
    java.lang.Class<?>[] types = {
            Integer.TYPE, Long.TYPE, Double.TYPE, Boolean.TYPE,
            Byte.TYPE, Character.TYPE, Short.TYPE, Void.TYPE
    };

    for (int i = 0; i < names.length; ++i) {
        com.fasterxml.jackson.databind.JavaType type = factory.constructFromCanonical(names[i]);
        org.junit.Assert.assertEquals(types[i], type.getRawClass());
    }
}

@org.junit.Test
public void constructFromCanonicalResolvesMapTypeArguments() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("java.util.Map<java.lang.String,java.lang.Integer>");

    org.junit.Assert.assertEquals(java.util.Map.class, type.getRawClass());
    org.junit.Assert.assertEquals(String.class, type.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void constructTypeResolvesWildcardUpperBound() {
    java.lang.reflect.Type reflectedType =
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<? extends Number>>() { }.getType();

    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType(reflectedType);

    org.junit.Assert.assertEquals(java.util.List.class, type.getRawClass());
    org.junit.Assert.assertEquals(Number.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void constructTypeResolvesGenericArrayVariableBound() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(genericArrayType());

    org.junit.Assert.assertTrue(type.isArrayType());
    org.junit.Assert.assertEquals(Object[].class, type.getRawClass());
    org.junit.Assert.assertEquals(Object.class, type.getContentType().getRawClass());
}

private <T> java.lang.reflect.Type genericArrayType() {
    return new com.fasterxml.jackson.core.type.TypeReference<T[]>() { }.getType();
}
}
