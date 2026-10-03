package com.google.gson.internal;

 import org.junit.Test;

 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 import java.lang.reflect.WildcardType;
 import java.util.ArrayList;
 import java.util.List;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 public class GsonTypesBug14Test {

     // Helper to get a TypeVariable from a class
     private static TypeVariable<?> getTypeVariable(Class<?> clazz, String name) {
         for (TypeVariable<?> tv : clazz.getTypeParameters()) {
             if (tv.getName().equals(name)) {
                 return tv;
             }
         }
         throw new IllegalArgumentException("TypeVariable " + name + " not found in " + clazz);
     }

     // ---------- Recursive type resolution (StackOverflowError bugs) ----------

     @Test
     public void testResolveComparableRecursively() {
         Type context = ComparableFoo.class;
         Class<?> contextRawType = ComparableFoo.class;
         TypeVariable<?> typeVarT = getTypeVariable(ComparableFoo.class, "T");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarT);
         assertTrue("Resolved type should be ComparableFoo", resolved instanceof Class);
         assertEquals(ComparableFoo.class, resolved);
     }

     @Test
     public void testResolveSelfReferencingTypeNoStackOverflow() {
         Type context = Node.class;
         Class<?> contextRawType = Node.class;
         TypeVariable<?> typeVarT = getTypeVariable(Node.class, "T");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarT);
         assertNotNull(resolved);
     }

     // ---------- Wildcard boundary simplification (duplicate bounds bugs) ----------

     @Test
     public void testResolveSuperWildcardCollapses() {
         TypeVariable<?> typeVarE = getTypeVariable(ArrayList.class, "E");
         Type resolved = $Gson$Types.resolve(SuperWildcardFoo.class, SuperWildcardFoo.class,
typeVarE);
         assertTrue("Resolved should be a WildcardType", resolved instanceof WildcardType);
         WildcardType wt = (WildcardType) resolved;
         assertEquals(1, wt.getLowerBounds().length);
         assertEquals(Number.class, wt.getLowerBounds()[0]);
     }

     @Test
     public void testResolveExtendsWildcardCollapses() {
         TypeVariable<?> typeVarE = getTypeVariable(ArrayList.class, "E");
         Type resolved = $Gson$Types.resolve(ExtendsWildcardFoo.class, ExtendsWildcardFoo.class,
typeVarE);
         assertTrue("Resolved should be a WildcardType", resolved instanceof WildcardType);
         WildcardType wt = (WildcardType) resolved;
         assertEquals(1, wt.getUpperBounds().length);
         assertEquals(Number.class, wt.getUpperBounds()[0]);
     }

     // ---------- Wildcard resolution on nested parameterized types ----------

     @Test
     public void testResolveSuperWildcardInParameterizedArgument() {
         TypeVariable<?> typeVarT = getTypeVariable(FooWithSuperBound.class, "T");
         Type resolved = $Gson$Types.resolve(FooWithSuperBound.class, FooWithSuperBound.class,
typeVarT);

         assertTrue(resolved instanceof ParameterizedType);
         ParameterizedType pt = (ParameterizedType) resolved;
         assertEquals(Bar.class, pt.getRawType());
         Type arg = pt.getActualTypeArguments()[0];
         assertTrue(arg instanceof WildcardType);
         WildcardType wt = (WildcardType) arg;
         assertEquals(1, wt.getLowerBounds().length);
         assertEquals(Number.class, wt.getLowerBounds()[0]);
         assertEquals(1, wt.getUpperBounds().length);
         assertEquals(Object.class, wt.getUpperBounds()[0]);
     }

     @Test
     public void testResolveExtendsWildcardInParameterizedArgument() {
         TypeVariable<?> typeVarT = getTypeVariable(FooWithExtendsBound.class, "T");
         Type resolved = $Gson$Types.resolve(FooWithExtendsBound.class, FooWithExtendsBound.class,
typeVarT);

         assertTrue(resolved instanceof ParameterizedType);
         ParameterizedType pt = (ParameterizedType) resolved;
         assertEquals(Bar.class, pt.getRawType());
         Type arg = pt.getActualTypeArguments()[0];
         assertTrue(arg instanceof WildcardType);
         WildcardType wt = (WildcardType) arg;
         assertEquals(1, wt.getUpperBounds().length);
         assertEquals(Number.class, wt.getUpperBounds()[0]);
     }

     // ---------- Mixed bounds resolution ----------

     @Test
     public void testResolveSuperOfExtendsCollapses() {
         Type inner = $Gson$Types.subtypeOf(Number.class);
         Type outer = $Gson$Types.supertypeOf(inner);
         Type canonical = $Gson$Types.canonicalize(outer);
         assertTrue(canonical instanceof WildcardType);
         WildcardType wt = (WildcardType) canonical;
         assertEquals(1, wt.getLowerBounds().length);
         assertTrue(wt.getLowerBounds()[0] instanceof WildcardType);
     }

     // ---------- Canonicalize and identity ----------

     @Test
     public void testCanonicalizePreservesSimpleWildcard() {
         WildcardType superNumber = $Gson$Types.supertypeOf(Number.class);
         Type canonical = $Gson$Types.canonicalize(superNumber);
         assertTrue($Gson$Types.equals(superNumber, canonical));
     }

     @Test
     public void testCanonicalizePreservesSubtypeOf() {
         WildcardType extendsNumber = $Gson$Types.subtypeOf(Number.class);
         Type canonical = $Gson$Types.canonicalize(extendsNumber);
         assertTrue($Gson$Types.equals(extendsNumber, canonical));
     }

     // ---------- resolve with array and raw types ----------

     @Test
     public void testResolveTypeVariableWithConcreteBinding() {
         Type context = new $Gson$Types.ParameterizedTypeImpl(null, List.class, String.class);
         Class<?> contextRawType = List.class;
         TypeVariable<?> typeVarE = getTypeVariable(List.class, "E");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarE);
         assertEquals(String.class, resolved);
     }

     // ---------- Helper types ----------

     static class ComparableFoo implements Comparable<ComparableFoo> {
         public int compareTo(ComparableFoo o) { return 0; }
     }

     static class Node<T extends Node<T>> {}

     static class Bar<T> {}

     static class FooWithSuperBound<T extends Bar<? super Number>> {}

     static class FooWithExtendsBound<T extends Bar<? extends Number>> {}

     static class SuperWildcardFoo extends ArrayList<? super Number> {}

     static class ExtendsWildcardFoo extends ArrayList<? extends Number> {}
 }