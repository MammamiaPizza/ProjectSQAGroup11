package com.fasterxml.jackson.databind.type;

import java.lang.reflect.Type;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TypeFactoryLocalGenericResolutionTest
{
    private static class Holder<V> { }

    private static class EntityContainer<T extends CharSequence> {
        class LocalEntity<U> extends Holder<T> { }
    }

    @Test
    public void testBoundedLocalTypeVariableWithClassContext()
    {
        class Local<T extends CharSequence> { }

        Type variable = Local.class.getTypeParameters()[0];
        JavaType resolved = TypeFactory.defaultInstance().constructType(variable, Local.class);

        assertEquals(CharSequence.class, resolved.getRawClass());
    }

    @Test
    public void testBoundedLocalTypeVariableWithJavaTypeContext()
    {
        class Local<T extends CharSequence> { }

        TypeFactory factory = TypeFactory.defaultInstance();
        Type variable = Local.class.getTypeParameters()[0];
        JavaType classContextResult = factory.constructType(variable, Local.class);
        JavaType javaTypeContextResult = factory.constructType(variable,
                factory.constructType(Local.class));

        assertEquals(CharSequence.class, javaTypeContextResult.getRawClass());
        assertEquals(classContextResult, javaTypeContextResult);
    }

    @Test
    public void testPartiallyParameterizedInnerTypeUsesEnclosingBound()
    {
        TypeFactory factory = TypeFactory.defaultInstance();
        Type parentType = EntityContainer.LocalEntity.class.getGenericSuperclass();

        JavaType resolved = factory.constructType(parentType, EntityContainer.class);

        assertEquals(Holder.class, resolved.getRawClass());
        assertNotNull(resolved.containedType(0));
        assertEquals(CharSequence.class, resolved.containedType(0).getRawClass());
    }

    @Test
    public void testPartiallyParameterizedInnerTypeUsesParameterizedEnclosingContext()
    {
        TypeFactory factory = TypeFactory.defaultInstance();
        Type parentType = EntityContainer.LocalEntity.class.getGenericSuperclass();
        JavaType context = factory.constructType(new TypeReference<EntityContainer<String>>() { }.getType());

        JavaType resolved = factory.constructType(parentType, context);

        assertEquals(Holder.class, resolved.getRawClass());
        assertNotNull(resolved.containedType(0));
        assertEquals(String.class, resolved.containedType(0).getRawClass());
    }
}
