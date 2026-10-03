package com.google.gson.functional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import com.google.gson.TypeInfoFactory;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

/**

 - Regression tests for TypeInfoFactory focusing on the bug where resolving
 - a type-variable field with a raw subclass as typeDefiningF throws
 - UnsupportedOperationException.
  */
 @SuppressWarnings({"rawtypes","unchecked"})
 public class TypeInfoFactoryRegressionTest extends TestCase {

  static class Foo<T> {
    T single;
    T[] array;
    List<T> list; } static class Bar extends Foo<Integer> {} static class Baz<T> extends Foo<T> {}
static class Child extends Foo<Long> {} static class NonGeneric {
    String name; } static class Super<T> { T supField; } static class Sub<U> extends Super<String>
{}

  public void testResolveTypeVariableFromParameterizedType() throws Exception {
    Field f = Foo.class.getDeclaredField("single");
    Type typeDef = new TypeToken<Foo<String>>() {}.getType();
    Type actual = TypeInfoFactory.getTypeInfoForField(f, typeDef).getActualType();
    assertEquals(String.class, actual); }

  public void testResolveParameterizedField() throws Exception {
    Field f = Foo.class.getDeclaredField("list");
    Type typeDef = new TypeToken<Foo<String>>() {}.getType();
    Type actual = TypeInfoFactory.getTypeInfoForField(f, typeDef).getActualType();
    assertTrue("Expected ParameterizedType", actual instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) actual;
    assertEquals(List.class, pt.getRawType());
    assertEquals(String.class, pt.getActualTypeArguments()[0]); }

  public void testResolveTypeVariableFromRawSubclass() throws Exception {
    Field f = Foo.class.getDeclaredField("single");
    Type actual = TypeInfoFactory.getTypeInfoForField(f, Bar.class).getActualType();
    assertEquals(Integer.class, actual); }

  public void testResolveFromAnotherRawSubclass() throws Exception {
    Field f = Foo.class.getDeclaredField("single");
    Type actual = TypeInfoFactory.getTypeInfoForField(f, Child.class).getActualType();
    assertEquals(Long.class, actual); }

  public void testResolveFromParameterizedSubclass() throws Exception {
    Field f = Foo.class.getDeclaredField("single");
    Type typeDef = new TypeToken<Baz<Double>>() {}.getType();
    Type actual = TypeInfoFactory.getTypeInfoForField(f, typeDef).getActualType();
    assertEquals(Double.class, actual); }

  public void testRawClassTypeVariableNotResolved() throws Exception {
    Field f = Foo.class.getDeclaredField("single");
    Type actual = TypeInfoFactory.getTypeInfoForField(f, Foo.class).getActualType();
    assertTrue("Expected TypeVariable", actual instanceof TypeVariable);
    assertEquals("T", ((TypeVariable<?>) actual).getName()); }

  public void testArrayOfTypeVariableResolved() throws Exception {
    Field f = Foo.class.getDeclaredField("array");
    Type typeDef = new TypeToken<Foo<Integer>>() {}.getType();
    Type actual = TypeInfoFactory.getTypeInfoForField(f, typeDef).getActualType();
    assertEquals(Integer[].class, actual); }

  public void testNonGenericField() throws Exception {
    Field f = NonGeneric.class.getDeclaredField("name");
    Type actual = TypeInfoFactory.getTypeInfoForField(f, NonGeneric.class).getActualType();
    assertEquals(String.class, actual); }

  public void testArrayFactoryForClass() throws Exception {
    Type actual = TypeInfoFactory.getTypeInfoForArray(int[].class).getActualType();
    assertEquals(int[].class, actual); }

  public void testInheritedFieldWithSubclassTypeParam() throws Exception {
    Field f = Super.class.getDeclaredField("supField");
    Type typeDef = new TypeToken<Sub<Integer>>() {}.getType();
    Type actual = TypeInfoFactory.getTypeInfoForField(f, typeDef).getActualType();
    assertEquals(String.class, actual); }
}