package org.apache.commons.lang3.reflect;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import java.lang.reflect.*;
 import java.util.*;

 public class TypeUtilsLANG775Test {

     // ---- helper types ----
     static class This<X, Y> {}
     static class Thing extends This<String, String> {}

     static class Single<Z> {}
     static class SubSingle extends Single<Integer> {}

     static class WildcardFields {
         List<Integer> intList;
         List<Number> numList;
         List<? extends Number> extNumList;
         List<? super Integer> supIntList;
         List<String> strList;
         List listRaw;
     }

     interface Bounded<T extends Comparable<T>> {}
     static class BoundedImpl implements Bounded<Integer> {}

     // -------------------- test methods ----

     @Test
     public void testGetTypeArgumentsWithTwoParams() throws Exception {
         Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(Thing.class, This.class);
         assertEquals(2, args.size());
         for (Map.Entry<TypeVariable<?>, Type> e : args.entrySet()) {
             assertEquals(String.class, e.getValue());
         }
     }

     @Test
     public void testIsAssignableConcreteSubtypeToSuperType() throws Exception {
         Type thingSuper = Thing.class.getGenericSuperclass();
         assertTrue(TypeUtils.isAssignable(Thing.class, thingSuper));
     }

     @Test
     public void testGetTypeArgumentsWithSingleParam() throws Exception {
         Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(SubSingle.class,
Single.class);
         assertEquals(1, args.size());
         assertEquals(Integer.class, args.values().iterator().next());
     }

     @Test
     public void testIsAssignableSubSingleToSingle() throws Exception {
         Type subSingleSuper = SubSingle.class.getGenericSuperclass();
         assertTrue(TypeUtils.isAssignable(SubSingle.class, subSingleSuper));
     }

     @Test
     public void testGetTypeArgumentsWithWildcardParameterizedType() throws Exception {
         Field f = WildcardFields.class.getDeclaredField("extNumList");
         Type type = f.getGenericType();
         assertTrue(type instanceof ParameterizedType);
         Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments((ParameterizedType) type);
         assertEquals(1, args.size());
         Type arg = args.values().iterator().next();
         assertTrue(arg instanceof WildcardType);
     }

     @Test
     public void testIsAssignableWildcardUpperBoundTrue() throws Exception {
         Type intListType = WildcardFields.class.getDeclaredField("intList").getGenericType();
         Type extNumListType = WildcardFields.class.getDeclaredField("extNumList").getGenericType();
         assertTrue(TypeUtils.isAssignable(intListType, extNumListType));
     }

     @Test
     public void testIsAssignableWildcardLowerBoundTrue() throws Exception {
         Type intListType = WildcardFields.class.getDeclaredField("intList").getGenericType();
         Type supIntListType = WildcardFields.class.getDeclaredField("supIntList").getGenericType();
         assertTrue(TypeUtils.isAssignable(intListType, supIntListType));
     }

     @Test
     public void testIsAssignableInvariantFalse() throws Exception {
         Type strListType = WildcardFields.class.getDeclaredField("strList").getGenericType();
         Type numListType = WildcardFields.class.getDeclaredField("numList").getGenericType();
         assertFalse(TypeUtils.isAssignable(strListType, numListType));
     }

     @Test
     public void testIsAssignableNullType() throws Exception {
         Type target = This.class; // any non-null
         assertTrue(TypeUtils.isAssignable(null, target));
     }

     @Test
     public void testGetTypeArgumentsNonAssignableReturnsNull() throws Exception {
         // String is not related to This
         Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(String.class, This.class);
         assertNull(args);
     }

     @Test
     public void testIsAssignableRawToParameterized() throws Exception {
         Type listRawType = WildcardFields.class.getDeclaredField("listRaw").getGenericType();
         Type intListType = WildcardFields.class.getDeclaredField("intList").getGenericType();
         // raw List is assignable to List<Integer>? Usually no, but raw is assignable to
parameterized types per JLS.
         assertTrue(TypeUtils.isAssignable(listRawType, intListType));
     }

     @Test
     public void testGetTypeArgumentsWithBoundedTypeVariable() throws Exception {
         Type implSuper = BoundedImpl.class.getGenericSuperclass(); // Bounded<Integer>
         // getTypeArguments(Type, Class<?>) for impl class to Bounded interface
         Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(BoundedImpl.class,
Bounded.class);
         assertEquals(1, args.size());
         for (Map.Entry<TypeVariable<?>, Type> e : args.entrySet()) {
             assertEquals(Integer.class, e.getValue());
         }
     }
 }
