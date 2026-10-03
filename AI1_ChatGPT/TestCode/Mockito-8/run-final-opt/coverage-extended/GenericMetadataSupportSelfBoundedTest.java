package org.mockito.internal.util.reflection;

import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericMetadataSupportSelfBoundedTest {

    private static class Box<T> {
    }

    private static class TypeHolder {
        Box<String> stringBox;
        Box<? extends Number> numberBox;
    }

    private static class SelfBounded<T extends SelfBounded<T>> {
    }

    private static class ComparableSelfBounded<T extends Comparable<T>> {
    }

    private static class MultipleBounds<T extends Number & Runnable> {
    }

    @Test
    public void inferFromSelfBoundedClassTerminatesAndKeepsBoundedTypeMetadata() {
        TypeVariable<?> typeVariable = SelfBounded.class.getTypeParameters()[0];

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(SelfBounded.class);
        Map<TypeVariable, Type> arguments = metadata.actualTypeArguments();

        assertEquals(SelfBounded.class, metadata.rawType());
        assertTrue(arguments.containsKey(typeVariable));
        assertTrue(arguments.get(typeVariable) instanceof GenericMetadataSupport.BoundedType);

        GenericMetadataSupport.BoundedType boundedType =
                (GenericMetadataSupport.BoundedType) arguments.get(typeVariable);
        assertTrue(boundedType.firstBound() instanceof ParameterizedType);

        ParameterizedType firstBound = (ParameterizedType) boundedType.firstBound();
        assertEquals(SelfBounded.class, firstBound.getRawType());
        assertArrayEquals(new Type[] { typeVariable }, firstBound.getActualTypeArguments());
    }

    @Test
    public void inferFromComparableSelfBoundedClassTerminatesAndExposesItsBound() {
        TypeVariable<?> typeVariable = ComparableSelfBounded.class.getTypeParameters()[0];

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(ComparableSelfBounded.class);
        Type resolved = metadata.actualTypeArguments().get(typeVariable);

        assertTrue(resolved instanceof GenericMetadataSupport.BoundedType);
        Type firstBound = ((GenericMetadataSupport.BoundedType) resolved).firstBound();
        assertTrue(firstBound instanceof ParameterizedType);
        assertEquals(Comparable.class, ((ParameterizedType) firstBound).getRawType());
        assertArrayEquals(new Type[] { typeVariable },
                ((ParameterizedType) firstBound).getActualTypeArguments());
    }

    @Test
    public void parameterizedTypeMapsItsTypeVariableToConcreteArgument() throws Exception {
        Field field = TypeHolder.class.getDeclaredField("stringBox");
        TypeVariable<?> typeVariable = Box.class.getTypeParameters()[0];

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(field.getGenericType());

        assertEquals(Box.class, metadata.rawType());
        assertEquals(String.class, metadata.actualTypeArguments().get(typeVariable));
        assertFalse(metadata.hasRawExtraInterfaces());
    }

    @Test
    public void wildcardArgumentIsRepresentedByItsBoundedType() throws Exception {
        Field field = TypeHolder.class.getDeclaredField("numberBox");
        TypeVariable<?> typeVariable = Box.class.getTypeParameters()[0];

        GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(field.getGenericType());
        Type resolved = metadata.actualTypeArguments().get(typeVariable);

        assertTrue(resolved instanceof GenericMetadataSupport.BoundedType);
        assertEquals(Number.class,
                ((GenericMetadataSupport.BoundedType) resolved).firstBound());
    }

    @Test
    public void typeVariableBoundedTypeSeparatesFirstAndInterfaceBounds() {
        TypeVariable<?> typeVariable = MultipleBounds.class.getTypeParameters()[0];

        GenericMetadataSupport.TypeVarBoundedType boundedType =
                new GenericMetadataSupport.TypeVarBoundedType(typeVariable);

        assertEquals(Number.class, boundedType.firstBound());
        assertArrayEquals(new Type[] { Runnable.class }, boundedType.interfaceBounds());
        assertEquals(typeVariable, boundedType.typeVariable());
    }

    @Test(expected = MockitoException.class)
    public void inferFromRejectsStandaloneTypeVariables() {
        GenericMetadataSupport.inferFrom(Box.class.getTypeParameters()[0]);
    }
}
