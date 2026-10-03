package com.google.javascript.rhino.jstype;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import com.google.javascript.rhino.ErrorReporter;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for {@link UnionType} focusing on the bug where getGreatestSubtype
  * returns NO_TYPE instead of NO_OBJECT_TYPE when one operand is a union
  * of NO_OBJECT_TYPE and NO_TYPE.
  */
 public class UnionTypeTest {

     private JSTypeRegistry registry;

     private JSType noObject;
     private JSType noType;
     private JSType numberType;
     private JSType stringType;
     private JSType nullType;
     private JSType unknownType;

     @Before
     public void setUp() {
         // Create a minimal error reporter since the production code expects one.
         ErrorReporter errorReporter = new ErrorReporter() {
             @Override public void warning(String message, String sourceName,
                                           int line, String lineSource, int lineOffset) {}
             @Override public void error(String message, String sourceName,
                                         int line, String lineSource, int lineOffset) {}
             @Override public com.google.javascript.rhino.EvalError runtimeError(
                 String message, String sourceName, int line, String lineSource, int lineOffset) {
                 return new com.google.javascript.rhino.EvalError(message, sourceName, line,
lineSource, lineOffset);
             }
         };
         registry = new JSTypeRegistry(errorReporter);

         noObject = registry.getNativeType(JSTypeNative.NO_OBJECT_TYPE);
         noType = registry.getNativeType(JSTypeNative.NO_TYPE);
         numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
         stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
         nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
         unknownType = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
     }

     // Helper to create a union from given types.
     private JSType union(JSType... types) {
         UnionTypeBuilder builder = new UnionTypeBuilder(registry);
         for (JSType t : types) {
             builder.addAlternate(t);
         }
         return builder.build();
     }

     /**
      * Bug reproducer: union(NO_OBJECT_TYPE, NO_TYPE) . getGreatestSubtype(numberType)
      * should return NO_OBJECT_TYPE, but the buggy code returns NO_TYPE.
      */
     @Test
     public void testGetGreatestSubtypeUnionOfNoObjectAndNone() {
         JSType noObjectUnionNone = union(noObject, noType);
         JSType result = noObjectUnionNone.getGreatestSubtype(numberType);
         assertNotNull(result);
         assertEquals(noObject, result);
     }

     /**
      * Symmetric case: other operand is union(NO_OBJECT_TYPE, NO_TYPE).
      */
     @Test
     public void testGetGreatestSubtypeUnionOfNoObjectAndNoneReverse() {
         JSType noObjectUnionNone = union(noObject, noType);
         JSType result = numberType.getGreatestSubtype(noObjectUnionNone);
         assertNotNull(result);
         assertEquals(noObject, result);
     }

     /**
      * When both operands are object types, the greatest subtype should be NO_OBJECT_TYPE
      * if there is no non-trivial common subtype.
      */
     @Test
     public void testGetGreatestSubtypeTwoObjectUnions() {
         JSType union1 = union(numberType, stringType);
         JSType union2 = union(numberType, stringType);
         JSType result = union1.getGreatestSubtype(union2);
         // The greatest subtype of two identical unions is the union itself.
         assertNotNull(result);
         assertTrue(result.isSubtype(union1));
         assertTrue(result.isSubtype(union2));
     }

     @Test
     public void testIsSubtypeWithNoType() {
         // NO_TYPE is a subtype of everything.
         assertTrue(noType.isSubtype(numberType));
         assertTrue(noType.isSubtype(noObject));
     }

     @Test
     public void testIsSubtypeUnionContainingNoType() {
         JSType U = union(noObject, noType);
         assertTrue(U.isSubtype(noObject));
         assertTrue(U.isSubtype(numberType));
     }

     @Test
     public void testCanAssignToUnionToSupertype() {
         JSType U = union(numberType, stringType);
         assertTrue(U.canAssignTo(numberType));
     }

     @Test
     public void testGetLeastSupertypeUnion() {
         JSType U = union(numberType, stringType);
         JSType least = U.getLeastSupertype(nullType);
         assertNotNull(least);
         assertTrue(U.isSubtype(least));
         assertTrue(nullType.isSubtype(least));
     }

     @Test
     public void testGetRestrictedUnion() {
         JSType U = union(numberType, stringType);
         JSType restricted = ((UnionType) U).getRestrictedUnion(numberType);
         // stringType is not a subtype of numberType, so it should be removed.
         assertFalse(restricted.isUnionType() && ((UnionType) restricted).contains(stringType));
     }

     @Test
     public void testGetTypesUnderEquality() {
         JSType U = union(numberType, stringType);
         TypePair pair = U.getTypesUnderEquality(numberType);
         assertNotNull(pair);
         assertNotNull(pair.typeA);
         assertNotNull(pair.typeB);
     }

     @Test
     public void testGetGreatestSubtypeWithNull() {
         JSType U = union(numberType, nullType);
         JSType result = U.getGreatestSubtype(numberType);
         assertNotNull(result);
         // The greatest subtype should be numberType (null is not a number).
         assertTrue(result.isSubtype(numberType));
     }

     @Test
     public void testGetGreatestSubtypeWithUnknown() {
         JSType U = union(numberType, unknownType);
         JSType result = U.getGreatestSubtype(numberType);
         assertNotNull(result);
         // With unknown type in the union, the greatest subtype is more conservative.
         assertTrue(result.isSubtype(U));
     }

     @Test
     public void testGetGreatestSubtypeEmptyUnion() {
         UnionTypeBuilder builder = new UnionTypeBuilder(registry);
         JSType empty = builder.build(); // may return null or a special type
         if (empty != null) {
             JSType result = empty.getGreatestSubtype(numberType);
             // No common subtype – should be NO_TYPE.
             assertNotNull(result);
         }
     }
 }