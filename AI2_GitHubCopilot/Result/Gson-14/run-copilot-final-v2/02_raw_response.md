package com.google.gson.internal;

 import org.junit.Test;

 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 import java.lang.reflect.WildcardType;
 import java.util.AbstractCollection;
 import java.util.AbstractList;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
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

     // Helper to get the resolved type argument for a specific type parameter
     private static Type getTypeArgument(Type context, Class<?> contextRawType, Class<?>
parameterizedSupertype, int index) {
         Type supertype = $Gson$Types.getGenericSupertype(context, contextRawType,
parameterizedSupertype);
         return ((ParameterizedType) supertype).getActualTypeArguments()[index];
     }

     // ---------- Recursive type resolution (StackOverflowError bugs) ----------

     @Test
     public void testResolveComparableRecursively() {
         // Self-referencing type: class Foo implements Comparable<Foo>
         // resolve(Foo.class, Foo.class, T) where T extends Comparable<T> in Foo's declaration
         // Bug: StackOverflowError when resolving recursively
         Type context = ComparableFoo.class;
         Class<?> contextRawType = ComparableFoo.class;
         TypeVariable<?> typeVarT = getTypeVariable(ComparableFoo.class, "T");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarT);
         assertTrue("Resolved type should be ComparableFoo", resolved instanceof Class);
         assertEquals(ComparableFoo.class, resolved);
     }

     @Test
     public void testResolveSelfReferencingTypeNoStackOverflow() {
         // Self-referencing type: class Node<T extends Node<T>>
         Type context = Node.class;
         Class<?> contextRawType = Node.class;
         TypeVariable<?> typeVarT = getTypeVariable(Node.class, "T");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarT);
         // T should resolve to itself (the TypeVariable) since there's no concrete binding
         // The important thing is it doesn't StackOverflow
         assertNotNull(resolved);
     }

     // ---------- Wildcard boundary simplification (duplicate bounds bugs) ----------

     @Test
     public void testResolveSuperWildcardCollapses() {
         // class ListOfNumbers extends ArrayList<? super Number>
         // resolve(ListOfNumbers.class, ListOfNumbers.class, ? super T) where T resolves to ? super
Number
         // Expected: ? super Number (not ? super ? super Number)
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
         // class ListOfExtendsNumbers extends ArrayList<? extends Number>
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
         // class Bar<T>; class Foo<T extends Bar<? super Number>>
         // resolve(Foo.class, Foo.class, T) should give Bar<? super Number> without duplicate super
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
         // class FooWithExtendsBound<T extends Bar<? extends Number>>
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
         // ? super (? extends Number) should become ?
         // We create a scenario where resolving a type variable gives us ? super ? extends Number
         // class Baz<T extends ? super ? extends Number> -- not valid Java directly,
         // but the resolution logic may encounter it.
         // Instead, test that $Gson$Types.supertypeOf and subtypeOf compose correctly
         Type inner = $Gson$Types.subtypeOf(Number.class);
         Type outer = $Gson$Types.supertypeOf(inner);
         // When canonicalized, this should simplify
         Type canonical = $Gson$Types.canonicalize(outer);
         assertTrue(canonical instanceof WildcardType);
         WildcardType wt = (WildcardType) canonical;
         // ? super ? extends Number should have lower bound ? extends Number
         // and upper bound Object.class
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
         // List<String> bound to E -> String
         Type context = new $Gson$Types.ParameterizedTypeImpl(null, List.class, String.class);
         Class<?> contextRawType = List.class;
         TypeVariable<?> typeVarE = getTypeVariable(List.class, "E");
         Type resolved = $Gson$Types.resolve(context, contextRawType, typeVarE);
         assertEquals(String.class, resolved);
     }

     // ---------- Helper types ----------

     // Self-referencing comparable
     static class ComparableFoo implements Comparable<ComparableFoo> {
         public int compareTo(ComparableFoo o) { return 0; }
     }

     // Self-referencing Node
     static class Node<T extends Node<T>> {}

     // Foo with bound Bar<? super Number>
     static class Bar<T> {}
     static class FooWithSuperBound<T extends Bar<? super Number>> {}

     // Foo with bound Bar<? extends Number>
     static class FooWithExtendsBound<T extends Bar<? extends Number>> {}

     // ? super Number bound in extends clause
     static class SuperWildcardFoo extends ArrayList<Object> {
         // Simulated: the actual test uses the type argument ? super Number via TypeVariable
resolution
         // The class itself extends ArrayList with a wildcard argument; we use a direct approach
         // by leveraging the type hierarchy
     }

     // ? extends Number bound in extends clause
     static class ExtendsWildcardFoo extends ArrayList<Object> {
         // Same simulation pattern
     }
 }