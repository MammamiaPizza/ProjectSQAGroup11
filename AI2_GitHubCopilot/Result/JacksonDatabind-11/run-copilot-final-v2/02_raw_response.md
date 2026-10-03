package com.fasterxml.jackson.databind.type;

 import java.lang.reflect.TypeVariable;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import com.fasterxml.jackson.databind.JavaType;

 /**
  * Tests reproducing and verifying behaviour for
  * JacksonDatabind bug 11 (databind#609).
  * Focuses on TypeFactory resolving type variables
  * to their upper bounds when no explicit binding exists.
  */
 public class TypeFactoryBug11Test {

     private static final TypeFactory FACTORY = TypeFactory.defaultInstance();

     // --- Helper class hierarchies for testing ---

     /** Base with bounded type variable T extends CharSequence. */
     public static class BoundBase<T extends CharSequence> { }

     /** Subclass that extends BoundBase raw (no type argument). */
     public static class RawSub extends BoundBase { }

     /** Simple generic class without bounds. */
     public static class Box<T> { }

     /** Subclass providing explicit String binding. */
     public static class StringBox extends Box<String> { }

     /** Intermediate generic which leaves T unresolved. */
     public static class Wrapper<T> extends Box<T> { }

     /** Concrete class fixing T to Integer. */
     public static class IntWrapper extends Wrapper<Integer> { }

     /**
      * 1. findTypeParameters with explicit binding: should return String,
      *    not Object.
      */
     @Test
     public void testFindTypeParametersExplicitBinding() {
         JavaType[] params = FACTORY.findTypeParameters(StringBox.class, Box.class);
         assertNotNull("Type parameters should not be null", params);
         assertEquals("Should have exactly 1 type parameter", 1, params.length);
         assertEquals("Resolved type should be String",
                 String.class, params[0].getRawClass());
     }

     /**
      * 2. findTypeParameters on raw subclass: the type variable T extends CharSequence
      *    should be resolved to CharSequence, NOT Object (databind#609).
      */
     @Test
     public void testFindTypeParametersRawSubclassResolvesToBound() {
         JavaType[] params = FACTORY.findTypeParameters(RawSub.class, BoundBase.class);
         assertNotNull("Type parameters should not be null", params);
         assertEquals("Should have exactly 1 type parameter", 1, params.length);
         assertEquals("Raw subclass should resolve T to its upper bound CharSequence",
                 CharSequence.class, params[0].getRawClass());
     }

     /**
      * 3. Calling constructType on a TypeVariable with a non-Object bound
      *    and null context must return the bound, not Object.
      */
     @Test
     public void testConstructTypeVariableWithBound() {
         @SuppressWarnings("unchecked")
         TypeVariable<?> tv = (TypeVariable<?>) BoundBase.class.getTypeParameters()[0];
         JavaType resolved = FACTORY.constructType(tv);
         assertEquals("Type variable T extends CharSequence must resolve to CharSequence",
                 CharSequence.class, resolved.getRawClass());
     }

     /**
      * 4. constructType on a TypeVariable with no explicit bound (implicit Object)
      *    and null context should return Object without throwing.
      */
     @Test
     public void testConstructTypeVariableWithoutBoundDoesNotThrow() {
         @SuppressWarnings("unchecked")
         TypeVariable<?> tv = (TypeVariable<?>) Box.class.getTypeParameters()[0];
         JavaType resolved = null;
         try {
             resolved = FACTORY.constructType(tv);
         } catch (Exception e) {
             fail("constructType on an unbounded type variable should not throw: " + e);
         }
         assertNotNull(resolved);
         // Expected to be Object (unknown type)
         assertEquals(Object.class, resolved.getRawClass());
     }

     /**
      * 5. Partial resolution through generic intermediate: Wrapper<T> extends Box<T>.
      *    For IntWrapper, Box<T> should resolve T = Integer.
      */
     @Test
     public void testFindTypeParametersThroughIntermediate() {
         JavaType[] params = FACTORY.findTypeParameters(IntWrapper.class, Box.class);
         assertNotNull("Type parameters should not be null", params);
         assertEquals("Should have 1 type parameter", 1, params.length);
         assertEquals("T should be resolved to Integer",
                 Integer.class, params[0].getRawClass());
     }

     /**
      * 6. Calling moreSpecificType on two compatible types should return the
      *    more specific one.
      */
     @Test
     public void testMoreSpecificType() {
         JavaType stringType = FACTORY.constructType(String.class);
         JavaType charSeqType = FACTORY.constructType(CharSequence.class);
         // String is more specific than CharSequence
         JavaType result = FACTORY.moreSpecificType(stringType, charSeqType);
         assertEquals("String should be more specific than CharSequence",
                 String.class, result.getRawClass());
         // Reverse order
         result = FACTORY.moreSpecificType(charSeqType, stringType);
         assertEquals("String should still be more specific when order reversed",
                 String.class, result.getRawClass());
     }

     /**
      * 7. findTypeParameters on a class that does not extend the expected type
      *    should either return null or an empty array. Verify it does not throw.
      */
     @Test
     public void testFindTypeParametersWrongBaseReturnsNull() {
         JavaType[] params = FACTORY.findTypeParameters(String.class, BoundBase.class);
         assertNull("Should return null when class does not extend expected type", params);
     }

     /**
      * 8. constructType with a fully parameterized generic superclass
      *    should produce a JavaType with correct raw class.
      */
     @Test
     public void testConstructTypeGenericSuperclass() {
         java.lang.reflect.Type genericSuper = StringBox.class.getGenericSuperclass();
         JavaType type = FACTORY.constructType(genericSuper);
         assertEquals("Raw class should be Box", Box.class, type.getRawClass());
     }

     /**
      * 9. verify that unknownType() consistently returns Object and is not null.
      */
     @Test
     public void testUnknownType() {
         JavaType unknown = TypeFactory.unknownType();
         assertNotNull("unknownType() must not be null", unknown);
         assertEquals(Object.class, unknown.getRawClass());
     }
 }