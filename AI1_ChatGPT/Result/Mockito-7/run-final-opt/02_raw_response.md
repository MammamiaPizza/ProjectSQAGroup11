package org.mockito.internal.util.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;
import org.mockito.Mockito;

public class GenericMetadataSupportRawNestedGenericsTest {

    interface Root<T extends Runnable> {
        Nested<T> nested();

        interface Nested<S extends Runnable> {
            S value();
        }
    }

    interface Direct<T extends Runnable> {
        T value();
    }

    interface MethodGeneric {
        <T extends Runnable> T create();
    }

    static class RunnableValue implements Runnable {
        @Override
        public void run() {
        }
    }

    static class GenericFields {
        Root<RunnableValue> concreteRoot;
        Root<? extends Runnable> wildcardRoot;
    }

    @Test
    public void resolvesRawNestedTypeVariableToItsBound() throws Exception {
        GenericMetadataSupport root = GenericMetadataSupport.inferFrom(Root.class);
        Method nestedMethod = Root.class.getMethod("nested");
        Method valueMethod = Root.Nested.class.getMethod("value");

        GenericMetadataSupport nested = root.resolveGenericReturnType(nestedMethod);
        GenericMetadataSupport value = nested.resolveGenericReturnType(valueMethod);

        assertEquals(Runnable.class, value.rawType());
    }

    @Test
    public void deepStubbingRawNestedGenericUsesBoundInsteadOfFailingRawExtraction() {
        Root root = Mockito.mock(Root.class, Mockito.RETURNS_DEEP_STUBS);

        Runnable value = root.nested().value();

        assertNotNull(value);
    }

    @Test
    public void resolvesConcreteParameterizedNestedTypeVariable() throws Exception {
        Field field = GenericFields.class.getDeclaredField("concreteRoot");
        GenericMetadataSupport root = GenericMetadataSupport.inferFrom(field.getGenericType());
        Method nestedMethod = Root.class.getMethod("nested");
        Method valueMethod = Root.Nested.class.getMethod("value");

        GenericMetadataSupport nested = root.resolveGenericReturnType(nestedMethod);
        GenericMetadataSupport value = nested.resolveGenericReturnType(valueMethod);

        assertEquals(RunnableValue.class, value.rawType());
    }

    @Test
    public void resolvesWildcardBoundForNestedGenericReturn() throws Exception {
        Field field = GenericFields.class.getDeclaredField("wildcardRoot");
        GenericMetadataSupport root = GenericMetadataSupport.inferFrom(field.getGenericType());
        Method nestedMethod = Root.class.getMethod("nested");
        Method valueMethod = Root.Nested.class.getMethod("value");

        GenericMetadataSupport nested = root.resolveGenericReturnType(nestedMethod);
        GenericMetadataSupport value = nested.resolveGenericReturnType(valueMethod);

        assertEquals(Runnable.class, value.rawType());
    }

    @Test
    public void resolvesRawDirectTypeVariableToItsBound() throws Exception {
        GenericMetadataSupport support = GenericMetadataSupport.inferFrom(Direct.class);
        Method valueMethod = Direct.class.getMethod("value");

        GenericMetadataSupport value = support.resolveGenericReturnType(valueMethod);

        assertEquals(Runnable.class, value.rawType());
    }

    @Test
    public void resolvesMethodTypeVariableToItsBound() throws Exception {
        GenericMetadataSupport support = GenericMetadataSupport.inferFrom(MethodGeneric.class);
        Method createMethod = MethodGeneric.class.getMethod("create");

        GenericMetadataSupport value = support.resolveGenericReturnType(createMethod);

        assertEquals(Runnable.class, value.rawType());
    }
}