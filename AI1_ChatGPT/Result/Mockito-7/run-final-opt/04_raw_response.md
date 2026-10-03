@org.junit.Test
public void exposesActualTypeArgumentsForConcreteParameterizedTypes() throws Exception {
    java.lang.reflect.Type type = ConcreteValueSource.class.getDeclaredField("value").getGenericType();

    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(type);

    java.util.Map<java.lang.reflect.TypeVariable, java.lang.reflect.Type> arguments =
            metadata.actualTypeArguments();

    org.junit.Assert.assertEquals(GenericValue.class, metadata.rawType());
    org.junit.Assert.assertEquals(1, arguments.size());
    org.junit.Assert.assertEquals(Runnable.class,
            arguments.get(GenericValue.class.getTypeParameters()[0]));
}

@org.junit.Test
public void resolvesIndirectTypeVariableBounds() throws Exception {
    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(RecursiveBound.class);

    org.mockito.internal.util.reflection.GenericMetadataSupport value =
            metadata.resolveGenericReturnType(RecursiveBound.class.getMethod("value"));

    org.junit.Assert.assertEquals(Runnable.class, value.rawType());
}

@org.junit.Test
public void resolvesWildcardBoundWhoseUpperBoundIsTypeVariable() throws Exception {
    java.lang.reflect.Type type = WildcardValueSource.class.getDeclaredField("value").getGenericType();

    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(type);

    org.mockito.internal.util.reflection.GenericMetadataSupport value =
            metadata.resolveGenericReturnType(GenericValue.class.getMethod("value"));

    org.junit.Assert.assertEquals(Runnable.class, value.rawType());
}

@org.junit.Test
public void supportsPlainReturnTypesAndRejectsUnsupportedTypeMetadata() throws Exception {
    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(PlainValue.class)
                    .resolveGenericReturnType(PlainValue.class.getMethod("value"));

    org.junit.Assert.assertEquals(String.class, metadata.rawType());
    org.junit.Assert.assertTrue(metadata.actualTypeArguments().isEmpty());
    org.junit.Assert.assertTrue(metadata.extraInterfaces().isEmpty());
    org.junit.Assert.assertFalse(metadata.hasRawExtraInterfaces());

    try {
        org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(
                new java.lang.reflect.Type() {
                });
        org.junit.Assert.fail("Expected unsupported Type metadata to be rejected");
    } catch (org.mockito.exceptions.base.MockitoException expected) {
    }
}

private interface GenericValue<T> {
    T value();
}

private static class ConcreteValueSource {
    GenericValue<Runnable> value;
}

private interface RecursiveBound<T extends Runnable, U extends T> {
    U value();
}

private static class WildcardValueSource<T extends Runnable> {
    GenericValue<? extends T> value;
}

private interface PlainValue {
    String value();
}