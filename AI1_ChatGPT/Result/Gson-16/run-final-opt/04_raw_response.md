@Test
public void canonicalizePreservesNestedGenericArrayAndWildcardStructure() throws Exception {
  class GenericHolder<T> {
    java.util.List<? super T>[] values;
    java.util.Map<java.lang.String, java.util.List<? extends T>> mapping;
  }

  java.lang.reflect.Type arrayType =
      GenericHolder.class.getDeclaredField("values").getGenericType();
  java.lang.reflect.Type canonicalArray =
      com.google.gson.internal.$Gson$Types.canonicalize(arrayType);

  org.junit.Assert.assertTrue(canonicalArray instanceof java.lang.reflect.GenericArrayType);
  org.junit.Assert.assertTrue(
      com.google.gson.internal.$Gson$Types.equals(arrayType, canonicalArray));

  java.lang.reflect.Type componentType =
      ((java.lang.reflect.GenericArrayType) canonicalArray).getGenericComponentType();
  org.junit.Assert.assertTrue(componentType instanceof java.lang.reflect.ParameterizedType);
  java.lang.reflect.Type wildcard =
      ((java.lang.reflect.ParameterizedType) componentType).getActualTypeArguments()[0];
  org.junit.Assert.assertTrue(wildcard instanceof java.lang.reflect.WildcardType);
  org.junit.Assert.assertEquals(
      GenericHolder.class.getTypeParameters()[0],
      ((java.lang.reflect.WildcardType) wildcard).getLowerBounds()[0]);

  java.lang.reflect.Type mapType =
      GenericHolder.class.getDeclaredField("mapping").getGenericType();
  java.lang.reflect.Type canonicalMap =
      com.google.gson.internal.$Gson$Types.canonicalize(mapType);
  org.junit.Assert.assertTrue(canonicalMap instanceof java.lang.reflect.ParameterizedType);
  org.junit.Assert.assertTrue(com.google.gson.internal.$Gson$Types.equals(mapType, canonicalMap));
}

@Test
public void getCollectionAndMapTypeArgumentsFollowGenericSuperclasses() {
  class StringList extends java.util.ArrayList<java.lang.String> {
  }
  class StringIntegerMap
      extends java.util.HashMap<java.lang.String, java.lang.Integer> {
  }

  org.junit.Assert.assertEquals(
      java.lang.String.class,
      com.google.gson.internal.$Gson$Types.getCollectionElementType(
          StringList.class, StringList.class));

  java.lang.reflect.Type[] mapTypes =
      com.google.gson.internal.$Gson$Types.getMapKeyAndValueTypes(
          StringIntegerMap.class, StringIntegerMap.class);
  org.junit.Assert.assertEquals(java.lang.String.class, mapTypes[0]);
  org.junit.Assert.assertEquals(java.lang.Integer.class, mapTypes[1]);

  java.lang.reflect.Type[] propertiesTypes =
      com.google.gson.internal.$Gson$Types.getMapKeyAndValueTypes(
          java.util.Properties.class, java.util.Properties.class);
  org.junit.Assert.assertEquals(java.lang.String.class, propertiesTypes[0]);
  org.junit.Assert.assertEquals(java.lang.String.class, propertiesTypes[1]);
}

@Test
public void getRawTypeHandlesGenericArraysWildcardsAndTypeVariables() throws Exception {
  class GenericHolder<T> {
    java.util.List<java.lang.String>[] values;
    java.util.List<? extends java.lang.Number> numbers;
  }

  java.lang.reflect.Type arrayType =
      GenericHolder.class.getDeclaredField("values").getGenericType();
  org.junit.Assert.assertEquals(
      java.util.List[].class,
      com.google.gson.internal.$Gson$Types.getRawType(arrayType));

  java.lang.reflect.Type wildcard =
      ((java.lang.reflect.ParameterizedType)
          GenericHolder.class.getDeclaredField("numbers").getGenericType())
          .getActualTypeArguments()[0];
  org.junit.Assert.assertEquals(
      java.lang.Number.class,
      com.google.gson.internal.$Gson$Types.getRawType(wildcard));

  org.junit.Assert.assertEquals(
      java.lang.Object.class,
      com.google.gson.internal.$Gson$Types.getRawType(
          GenericHolder.class.getTypeParameters()[0]));
}

@Test
public void resolveSubstitutesTypeVariablesInsideLowerBoundWildcards() throws Exception {
  class Holder<T> {
    java.util.List<? super T> values;
  }
  class StringHolder extends Holder<java.lang.String> {
  }

  java.lang.reflect.Type unresolved =
      Holder.class.getDeclaredField("values").getGenericType();
  java.lang.reflect.Type resolved =
      com.google.gson.internal.$Gson$Types.resolve(
          StringHolder.class, StringHolder.class, unresolved);

  org.junit.Assert.assertTrue(resolved instanceof java.lang.reflect.ParameterizedType);
  java.lang.reflect.Type wildcard =
      ((java.lang.reflect.ParameterizedType) resolved).getActualTypeArguments()[0];
  org.junit.Assert.assertTrue(wildcard instanceof java.lang.reflect.WildcardType);
  org.junit.Assert.assertEquals(
      java.lang.String.class,
      ((java.lang.reflect.WildcardType) wildcard).getLowerBounds()[0]);
}