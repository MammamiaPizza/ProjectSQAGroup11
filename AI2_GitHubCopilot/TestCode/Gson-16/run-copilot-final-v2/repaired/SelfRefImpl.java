package com.google.gson.internal;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.lang.reflect.*;
 import java.util.*;

 /**
  * Tests for recursive type-variable resolution in {@link $Gson$Types}.
  * Targets the bug that causes {@link StackOverflowError} when resolving
  * TypeVariables with self-referential or mutually recursive bounds.
  */
 public class $Gson$TypesRecursiveTest {

     // ---------- helper types for testing ----------

     /** Simple self-referential bound: T extends SelfRef<T>. */
     static interface SelfRef<T extends SelfRef<T>> {}
     static class SelfRefImpl implements SelfRef<SelfRefImpl> {}

     /** Common recursive bound: T extends Comparable<T>. */
     static interface RecCmp<T extends Comparable<T>> {}
     static class RecCmpImpl implements RecCmp<RecCmpImpl>, Comparable<RecCmpImpl> {
         @Override public int compareTo(RecCmpImpl o) { return 0; }
     }

     /** Mutual recursion: A<X extends B<?>>, B<Y extends A<?>>. */
     static interface A<X extends B<?>> {}
     static interface B<Y extends A<?>> {}
     static class ABImpl implements A<BImpl>, B<AImpl> {}
     static class AImpl implements A<BImpl> {}
     static class BImpl implements B<AImpl> {}

     /** Deep cyclic chain (depth 5). */
     static interface C1<X extends C2<X>> {}
     static interface C2<Y extends C3<Y>> {}
     static interface C3<Z extends C4<Z>> {}
     static interface C4<W extends C5<W>> {}
     static interface C5<V extends C1<V>> {}
     static class CImpl implements C1<CImpl>, C2<CImpl>, C3<CImpl>, C4<CImpl>, C5<CImpl> {}

     /** Type variable with multiple bounds. */
     static interface MultiBound<T extends Number & Comparable<T>> {}
     static class MultiBoundImpl extends Number implements MultiBound<MultiBoundImpl>,
 Comparable<MultiBoundImpl> {
         @Override public int intValue() { return 0; }
         @Override public long longValue() { return 0; }
         @Override public float floatValue() { return 0; }
         @Override public double doubleValue() { return 0; }
         @Override public int compareTo(MultiBoundImpl o) { return 0; }
     }

     /** Non-recursive generic type for regression test. */
     static interface Simple<I> {}
     static class SimpleImpl implements Simple<String> {}

     // ---------- test methods ----------

     /** Self-recursive bound T extends SelfRef<T> - resolution must not overflow. */
     @Test public void testSelfRefBound() {
         TypeVariable<?> tv = SelfRef.class.getTypeParameters()[0];
         Type context = SelfRefImpl.class.getGenericInterfaces()[0];
         Type resolved = resolveSafely(context, SelfRefImpl.class, tv);
         assertEquals(SelfRefImpl.class, resolved);
     }

     /** Self-recursive bound T extends Comparable<T> - common real-world pattern. */
     @Test public void testComparableBound() {
         TypeVariable<?> tv = RecCmp.class.getTypeParameters()[0];
         Type context = RecCmpImpl.class.getGenericInterfaces()[0];
         Type resolved = resolveSafely(context, RecCmpImpl.class, tv);
         assertEquals(RecCmpImpl.class, resolved);
     }

     /** Mutual recursion across two interfaces A and B. */
     @Test public void testMutualRecursion() {
         TypeVariable<?> tv = A.class.getTypeParameters()[0];
         Type context = AImpl.class.getGenericInterfaces()[0];  // A<BImpl>
         Type resolved = resolveSafely(context, AImpl.class, tv);
         assertEquals(BImpl.class, resolved);
     }

     /** Deep cyclic chain of length 5 - exercises boundary of finite recursion. */
     @Test public void testDeepCyclicChain() {
         TypeVariable<?> tv = C1.class.getTypeParameters()[0];
         Type context = CImpl.class.getGenericInterfaces()[0];  // C1<CImpl>
         Type resolved = resolveSafely(context, CImpl.class, tv);
         assertEquals(CImpl.class, resolved);
     }

     /** Type variable with multiple upper bounds. */
     @Test public void testMultipleUpperBounds() {
         TypeVariable<?> tv = MultiBound.class.getTypeParameters()[0];
         Type context = MultiBoundImpl.class.getGenericInterfaces()[0];
         Type resolved = resolveSafely(context, MultiBoundImpl.class, tv);
         assertEquals(MultiBoundImpl.class, resolved);
     }

     /** Non-recursive generic type - must not break normal resolution. */
     @Test public void testSimpleResolution() {
         TypeVariable<?> tv = Simple.class.getTypeParameters()[0];
         Type context = SimpleImpl.class.getGenericInterfaces()[0];
         Type resolved = resolveSafely(context, SimpleImpl.class, tv);
         assertEquals(String.class, resolved);
     }

     /** Resolving a TypeVariable that does not belong to the context should throw. */
     @Test(expected = IllegalArgumentException.class)
     public void testForeignTypeVariable() {
         // SelfRef's T is not declared on Simple
         TypeVariable<?> tv = SelfRef.class.getTypeParameters()[0];
         Type context = SimpleImpl.class.getGenericInterfaces()[0];
         $Gson$Types.resolve(context, SimpleImpl.class, tv);
     }

     /** A resolved type must be canonical (no StackOverflow during canonicalization either). */
     @Test public void testCanonicalizeDoesNotOverflow() {
         ParameterizedType ctx = (ParameterizedType) SimpleImpl.class.getGenericInterfaces()[0];
         Type can = canonicalizeSafely(ctx);
         assertNotNull(can);
     }

     /** Raw type of a self-recursive parameterized type must not overflow. */
     @Test public void testGetRawTypeOnRecursive() {
         ParameterizedType ctx = (ParameterizedType) SelfRefImpl.class.getGenericInterfaces()[0];
         Class<?> raw = getRawTypeSafely(ctx);
         assertEquals(SelfRef.class, raw);
     }

     // ---------- safety wrappers ----------

     private Type resolveSafely(Type context, Class<?> rawType, TypeVariable<?> tv) {
         try {
             return $Gson$Types.resolve(context, rawType, tv);
         } catch (StackOverflowError soe) {
             fail("Recursive type resolution caused StackOverflowError");
             return null; // unreachable
         }
     }

     private Type canonicalizeSafely(Type type) {
         try {
             return $Gson$Types.canonicalize(type);
         } catch (StackOverflowError soe) {
             fail("canonicalize caused StackOverflowError");
             return null;
         }
     }

     private Class<?> getRawTypeSafely(Type type) {
         try {
             return $Gson$Types.getRawType(type);
         } catch (StackOverflowError soe) {
             fail("getRawType caused StackOverflowError");
             return null;
         }
     }
 }
