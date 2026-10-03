@Test
public void inferFromClassHasNoExtraInterfaces() {
    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(String.class);

    org.junit.Assert.assertTrue(metadata.extraInterfaces().isEmpty());
    org.junit.Assert.assertFalse(metadata.hasRawExtraInterfaces());
    org.junit.Assert.assertEquals(0, metadata.rawExtraInterfaces().length);
}

@Test
public void resolveGenericReturnTypeHandlesClassParameterizedAndTypeVariableReturns() throws Exception {
    class Returns {
        public String stringValue() {
            return null;
        }

        public java.util.List<String> stringList() {
            return null;
        }

        public <T> T genericValue() {
            return null;
        }
    }

    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(Returns.class);

    org.junit.Assert.assertEquals(String.class,
            metadata.resolveGenericReturnType(Returns.class.getMethod("stringValue")).rawType());

    org.mockito.internal.util.reflection.GenericMetadataSupport listReturn =
            metadata.resolveGenericReturnType(Returns.class.getMethod("stringList"));
    org.junit.Assert.assertEquals(java.util.List.class, listReturn.rawType());
    org.junit.Assert.assertEquals(String.class,
            listReturn.actualTypeArguments().get(java.util.List.class.getTypeParameters()[0]));

    org.junit.Assert.assertEquals(Object.class,
            metadata.resolveGenericReturnType(Returns.class.getMethod("genericValue")).rawType());
}

@Test
public void resolveGenericReturnTypeFollowsTypeVariableMappingsThroughSuperclass() throws Exception {
    class Parent<T> {
        public T value() {
            return null;
        }
    }
    class Child<U> extends Parent<U> {
    }
    class Carrier {
        public Child<String> child() {
            return null;
        }
    }

    org.mockito.internal.util.reflection.GenericMetadataSupport metadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(
                    Carrier.class.getMethod("child").getGenericReturnType());

    org.junit.Assert.assertEquals(String.class,
            metadata.resolveGenericReturnType(Parent.class.getMethod("value")).rawType());
}

@Test
public void inferFromFollowsIndirectTypeVariableAndWildcardBounds() throws Exception {
    class IndirectBounds<U extends Number, T extends U> {
    }
    class WildcardReturns<T> {
        public java.util.List<? extends T> values() {
            return null;
        }
    }

    org.mockito.internal.util.reflection.GenericMetadataSupport indirectMetadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(IndirectBounds.class);
    java.lang.reflect.Type indirectBound =
            indirectMetadata.actualTypeArguments().get(IndirectBounds.class.getTypeParameters()[1]);

    org.junit.Assert.assertTrue(
            indirectBound instanceof org.mockito.internal.util.reflection.GenericMetadataSupport.BoundedType);
    org.junit.Assert.assertEquals(Number.class,
            ((org.mockito.internal.util.reflection.GenericMetadataSupport.BoundedType) indirectBound).firstBound());

    org.mockito.internal.util.reflection.GenericMetadataSupport wildcardMetadata =
            org.mockito.internal.util.reflection.GenericMetadataSupport.inferFrom(
                    WildcardReturns.class.getMethod("values").getGenericReturnType());
    java.lang.reflect.Type wildcardBound =
            wildcardMetadata.actualTypeArguments().get(java.util.List.class.getTypeParameters()[0]);

    org.junit.Assert.assertTrue(
            wildcardBound instanceof org.mockito.internal.util.reflection.GenericMetadataSupport.BoundedType);
    org.junit.Assert.assertEquals(Object.class,
            ((org.mockito.internal.util.reflection.GenericMetadataSupport.BoundedType) wildcardBound).firstBound());
}