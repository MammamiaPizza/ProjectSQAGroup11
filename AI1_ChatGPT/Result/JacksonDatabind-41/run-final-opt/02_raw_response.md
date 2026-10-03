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
    @Test
    public void constructTypeWithNullUsesUnknownType()
    {
        JavaType type = TypeFactory.defaultInstance().constructType((Type) null);

        assertNotNull(type);
        assertEquals(Object.class, type.getRawClass());
    }

    @Test
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
}