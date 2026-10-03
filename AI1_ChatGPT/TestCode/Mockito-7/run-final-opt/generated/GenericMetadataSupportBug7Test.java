package org.mockito.internal.util.reflection;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class GenericMetadataSupportBug7Test {

    interface Holder<T> {
        T value();
    }

    interface NumberHolder<T extends Number> {
        T value();
    }

    interface RawLeaf<T> {
        T value();
    }

    interface RawMiddle<T> {
        RawLeaf<T> leaf();
    }

    interface RawRoot {
        RawMiddle middle();
    }

    static class ParameterizedSignatures {
        Holder<String> strings;
        Holder<? extends Number> numbers;
    }

    static class OrdinarySignatures {
        String text() {
            return "text";
        }
    }

    @Test
    public void resolvesConcreteParameterizedReturnType() throws Exception {
        Type type = field("strings").getGenericType();

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(type);
        GenericMetadataSupport returnType = metadata.resolveGenericReturnType(method(Holder.class, "value"));

        assertEquals(String.class, returnType.rawType());
    }

    @Test
    public void resolvesDeclaredTypeVariableBoundForRawGenericType() throws Exception {
        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(NumberHolder.class);

        GenericMetadataSupport returnType = metadata.resolveGenericReturnType(method(NumberHolder.class, "value"));

        assertEquals(Number.class, returnType.rawType());
    }

    @Test
    public void resolvesWildcardUpperBoundForGenericReturnType() throws Exception {
        Type type = field("numbers").getGenericType();

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(type);
        GenericMetadataSupport returnType = metadata.resolveGenericReturnType(method(Holder.class, "value"));

        assertEquals(Number.class, returnType.rawType());
    }

    @Test
    public void resolvesUnboundedTypeVariableFromRawGenericTypeToObject() throws Exception {
        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(Holder.class);

        GenericMetadataSupport returnType = metadata.resolveGenericReturnType(method(Holder.class, "value"));

        assertEquals(Object.class, returnType.rawType());
    }

    @Test
    public void resolvesNestedTypeVariableReachedThroughRawGenericType() throws Exception {
        GenericMetadataSupport middleMetadata = GenericMetadataSupport.inferFrom(RawMiddle.class);
        GenericMetadataSupport leafMetadata =
                middleMetadata.resolveGenericReturnType(method(RawMiddle.class, "leaf"));

        GenericMetadataSupport valueMetadata =
                leafMetadata.resolveGenericReturnType(method(RawLeaf.class, "value"));

        assertEquals(Object.class, valueMetadata.rawType());
    }

    @Test
    public void deepStubbingNestedRawGenericTypeDoesNotFailDuringGenericDiscovery() {
        RawRoot root = Mockito.mock(RawRoot.class, Mockito.RETURNS_DEEP_STUBS);
        RawLeaf leaf = root.middle().leaf();
        Object expected = new Object();

        Mockito.when(leaf.value()).thenReturn(expected);

        assertSame(expected, leaf.value());
    }

    @Test
    public void resolvesNonGenericMethodReturnType() throws Exception {
        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(OrdinarySignatures.class);

        GenericMetadataSupport returnType =
                metadata.resolveGenericReturnType(method(OrdinarySignatures.class, "text"));

        assertEquals(String.class, returnType.rawType());
    }

    @Test(expected = MockitoException.class)
    public void rejectsTypeVariablesAsInferenceRoots() {
        GenericMetadataSupport.inferFrom(Holder.class.getTypeParameters()[0]);
    }

    private static Field field(String name) throws Exception {
        return ParameterizedSignatures.class.getDeclaredField(name);
    }

    private static Method method(Class<?> type, String name) throws Exception {
        return type.getMethod(name);
    }
}
