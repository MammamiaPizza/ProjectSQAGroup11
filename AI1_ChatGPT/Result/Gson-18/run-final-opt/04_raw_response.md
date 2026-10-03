@org.junit.Test
public void getCollectionElementTypeResolvesConcreteTypeThroughGenericInterfaces() {
  java.lang.reflect.Type elementType =
      com.google.gson.internal.$Gson$Types.getCollectionElementType(
          IntegerCollection.class, IntegerCollection.class);

  org.junit.Assert.assertSame(java.lang.Integer.class, elementType);
}

@org.junit.Test
public void getMapKeyAndValueTypesResolveConcreteTypesThroughGenericSuperclass() {
  java.lang.reflect.Type[] keyAndValueTypes =
      com.google.gson.internal.$Gson$Types.getMapKeyAndValueTypes(
          StringIntegerMap.class, StringIntegerMap.class);

  org.junit.Assert.assertSame(java.lang.String.class, keyAndValueTypes[0]);
  org.junit.Assert.assertSame(java.lang.Integer.class, keyAndValueTypes[1]);
}

@org.junit.Test
public void arrayOfCreatesGenericArrayWithExpectedComponentAndRawType() {
  java.lang.reflect.Type arrayType =
      com.google.gson.internal.$Gson$Types.arrayOf(java.lang.String.class);

  org.junit.Assert.assertSame(
      java.lang.String.class,
      com.google.gson.internal.$Gson$Types.getArrayComponentType(arrayType));
  org.junit.Assert.assertSame(
      java.lang.String[].class,
      com.google.gson.internal.$Gson$Types.getRawType(arrayType));
  org.junit.Assert.assertTrue(
      com.google.gson.internal.$Gson$Types.equals(
          arrayType, com.google.gson.internal.$Gson$Types.canonicalize(arrayType)));
}

@org.junit.Test
public void wildcardFactoriesExposeExpectedBounds() {
  java.lang.reflect.WildcardType subtype =
      com.google.gson.internal.$Gson$Types.subtypeOf(java.lang.Number.class);
  java.lang.reflect.WildcardType supertype =
      com.google.gson.internal.$Gson$Types.supertypeOf(java.lang.Integer.class);

  org.junit.Assert.assertSame(java.lang.Number.class, subtype.getUpperBounds()[0]);
  org.junit.Assert.assertEquals(0, subtype.getLowerBounds().length);
  org.junit.Assert.assertSame(java.lang.Object.class, supertype.getUpperBounds()[0]);
  org.junit.Assert.assertSame(java.lang.Integer.class, supertype.getLowerBounds()[0]);
}

private interface GenericCollection<E> extends java.util.Collection<E> {
}

private interface IntegerCollection extends GenericCollection<java.lang.Integer> {
}

private static class GenericMap<K, V> extends java.util.HashMap<K, V> {
}

private static class StringIntegerMap extends GenericMap<java.lang.String, java.lang.Integer> {
}