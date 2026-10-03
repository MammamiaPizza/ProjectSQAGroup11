package com.google.gson.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

@SuppressWarnings({"unused", "rawtypes"})
public class GsonTypesTest {

  static final class SmallClass { }

  static final class ArrayListOfString extends ArrayList<String> { }

  static final class UnsupportedType implements Type { }

  interface Single<T> { }

  static final class SingleChild implements Single<List<String>> { }

  static final class GenericHolder<T> { }

  static final class ConcreteHolder extends GenericHolder<List<String>> { }

  private static List<String> listOfString; private static List<? extends Number> numberList;
private static Map<Long, SmallClass> mapOfLongToSmall; private static List<Map<String, SmallClass>>
listOfMaps;

  private static Type genericTypeOf(String fieldName) {
    try {
      return GsonTypesTest.class.getDeclaredField(fieldName).getGenericType();
    } catch (NoSuchFieldException e) {
      throw new AssertionError(e);
    } }

  @Test public void testGetCollectionElementTypeFromSubclass() {
    Type element = $Gson$Types.getCollectionElementType(ArrayListOfString.class,
ArrayListOfString.class);
    assertEquals(String.class, element); }

  @Test public void testGetCollectionElementTypeFromParameterizedList() {
    Type context = genericTypeOf("listOfString");
    Type element = $Gson$Types.getCollectionElementType(context, $Gson$Types.getRawType(context));
    assertEquals(String.class, element); }

  @Test public void testGetCollectionElementTypeIssue1107Shape() {
    Type context = genericTypeOf("listOfMaps");
    Type element = $Gson$Types.getCollectionElementType(context, $Gson$Types.getRawType(context));

 assertTrue(element instanceof ParameterizedType);
 assertEquals(Map.class, $Gson$Types.getRawType(element));

 Type[] actualTypeArguments = ((ParameterizedType) element).getActualTypeArguments();
 assertEquals(String.class, actualTypeArguments[0]);
 assertEquals(SmallClass.class, actualTypeArguments[1]); }

  @Test public void testGetCollectionElementTypeWildcardUpperBound() {
    Type context = genericTypeOf("numberList");
    Type element = $Gson$Types.getCollectionElementType(context, $Gson$Types.getRawType(context));

 assertTrue(element instanceof WildcardType);
 assertEquals(Number.class, ((WildcardType) element).getUpperBounds()[0]); }

  @Test public void testGetCollectionElementTypeRawCollection() {
    Type element = $Gson$Types.getCollectionElementType(ArrayList.class, ArrayList.class);
    assertEquals(Object.class, element); }

  @Test public void testGetMapKeyAndValueTypesForProperties() {
    Type[] keyAndValue = $Gson$Types.getMapKeyAndValueTypes(Properties.class, Properties.class);
    assertArrayEquals(new Type[] { String.class, String.class }, keyAndValue); }

  @Test public void testGetMapKeyAndValueTypesParameterized() {
    Type context = genericTypeOf("mapOfLongToSmall");
    Type[] keyAndValue = $Gson$Types.getMapKeyAndValueTypes(context,
$Gson$Types.getRawType(context));

 assertEquals(Long.class, keyAndValue[0]);
 assertEquals(SmallClass.class, keyAndValue[1]); }

  @Test public void testResolveTypeVariableDeclaredOnInterface() {
    TypeVariable<?> typeVariable = Single.class.getTypeParameters()[0];
    Type resolved = $Gson$Types.resolve(SingleChild.class, SingleChild.class, typeVariable);

 assertTrue(resolved instanceof ParameterizedType);
 assertEquals(List.class, $Gson$Types.getRawType(resolved));
 assertEquals(String.class, ((ParameterizedType) resolved).getActualTypeArguments()[0]); }

  @Test public void testGetSupertypeForGenericSuperclass() {
    Type supertype = $Gson$Types.getSupertype(ConcreteHolder.class, ConcreteHolder.class,
GenericHolder.class);

 assertTrue(supertype instanceof ParameterizedType);
 assertEquals(GenericHolder.class, $Gson$Types.getRawType(supertype));

 Type typeArgument = ((ParameterizedType) supertype).getActualTypeArguments()[0];
 assertEquals(List.class, $Gson$Types.getRawType(typeArgument));
 assertEquals(String.class, ((ParameterizedType) typeArgument).getActualTypeArguments()[0]); }

  @Test public void testGetRawTypeRejectsUnsupportedType() {
    try {
      $Gson$Types.getRawType(new UnsupportedType());
      fail("Expected IllegalArgumentException for an unsupported Type implementation");
    } catch (IllegalArgumentException expected) {
      assertNotNull(expected.getMessage());
    } }

  @Test public void testWildcardFactories() {
    WildcardType subtype = $Gson$Types.subtypeOf(Number.class);
    assertArrayEquals(new Type[] { Number.class }, subtype.getUpperBounds());
    assertArrayEquals(new Type[] {}, subtype.getLowerBounds());

 WildcardType supertype = $Gson$Types.supertypeOf(Number.class);
 assertArrayEquals(new Type[] { Object.class }, supertype.getUpperBounds());
 assertArrayEquals(new Type[] { Number.class }, supertype.getLowerBounds()); }

}