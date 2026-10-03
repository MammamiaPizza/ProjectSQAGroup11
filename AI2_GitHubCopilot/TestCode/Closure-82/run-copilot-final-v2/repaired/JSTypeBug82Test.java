package com.google.javascript.rhino.jstype;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import junit.framework.TestCase;

 /**
  * Tests for Closure bug 82: isEquivalentTo and isSubtype
  * do not correctly handle empty function types.
  */
 public class JSTypeBug82Test extends TestCase {
   private JSTypeRegistry registry;
   private JSType emptyFunc1;
   private JSType emptyFunc2;
   private JSType allType;
   private JSType noType;
   private JSType nullType;
   private JSType voidType;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     Compiler compiler = new Compiler();
     registry = compiler.getTypeRegistry();
     // Two distinct empty function types (structural equivalency expected)
     emptyFunc1 = registry.createFunctionType(
         registry.getNativeType(JSTypeNative.UNKNOWN_TYPE));
     emptyFunc2 = registry.createFunctionType(
         registry.getNativeType(JSTypeNative.UNKNOWN_TYPE));
     allType = registry.getNativeType(JSTypeNative.ALL_TYPE);
     noType = registry.getNativeType(JSTypeNative.NO_TYPE);
     nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
   }

   /**
    * Two empty function types should be equivalent.
    * Bug: default isEquivalentTo uses identity and fails.
    */
   public void testTwoEmptyFunctionTypesAreEquivalent() {
     assertTrue("Empty function types should be equivalent",
         emptyFunc1.isEquivalentTo(emptyFunc2));
   }

   /**
    * An empty function type should be equivalent to itself.
    */
   public void testEmptyFunctionTypeIsEquivalentToItself() {
     assertTrue("Empty function should be equivalent to itself",
         emptyFunc1.isEquivalentTo(emptyFunc1));
   }

   /**
    * Static helper should also report equivalence.
    */
   public void testStaticIsEquivalentEmptyFunctions() {
     assertTrue("Static isEquivalent should see empty functions equal",
         JSType.isEquivalent(emptyFunc1, emptyFunc2));
   }

   /**
    * An empty function type should be a subtype of itself.
    */
   public void testEmptyFunctionTypeIsSubtypeOfItself() {
     assertTrue("Empty function should be subtype of itself",
         emptyFunc1.isSubtype(emptyFunc1));
   }

   /**
    * One empty function type should be subtype of another empty one.
    */
   public void testEmptyFunctionTypeIsSubtypeOfAnotherEmpty() {
     assertTrue("Empty function should be subtype of another empty",
         emptyFunc1.isSubtype(emptyFunc2));
   }

   /**
    * An empty function type should be a subtype of AllType (top type).
    */
   public void testEmptyFunctionTypeIsSubtypeOfAllType() {
     assertTrue("Empty function should be subtype of AllType",
         emptyFunc1.isSubtype(allType));
   }

   /**
    * NoType should be a subtype of an empty function type (bottom).
    */
   public void testNoTypeIsSubtypeOfEmptyFunction() {
     assertTrue("NoType (bottom) should be subtype of empty function",
         noType.isSubtype(emptyFunc1));
   }

   /**
    * Null type should not be subtype of a non-nullable empty function.
    */
   public void testNullTypeIsNotSubtypeOfEmptyFunction() {
     assertFalse("Null should not be subtype of empty function",
         nullType.isSubtype(emptyFunc1));
   }

   /**
    * Void type is not equivalent to an empty function type.
    */
   public void testEmptyFunctionNotEquivalentToVoid() {
     assertFalse("Empty function should not be equivalent to void",
         emptyFunc1.isEquivalentTo(voidType));
   }

   /**
    * Equals contract: two equivalent empty functions must be equal.
    */
   public void testEqualsConsistentWithIsEquivalentTo() {
     assertTrue("Empty functions should be equal via equals()",
         emptyFunc1.equals(emptyFunc2));
   }

   /**
    * Nested empty function types: function returning empty should
    * be equivalent to another such function.
    */
   public void testNestedEmptyFunctionTypesAreEquivalent() {
     JSType nested1 = registry.createFunctionType(emptyFunc1);
     JSType nested2 = registry.createFunctionType(emptyFunc2);
     assertTrue("Nested empty functions should be equivalent",
         nested1.isEquivalentTo(nested2));
   }

   /**
    * Non-empty (different return) functions should not be equivalent.
    */
   public void testEmptyVsNonEmptyFunctionNotEquivalent() {
     JSType nonEmpty = registry.createFunctionType(voidType);
     assertFalse("Empty vs non-empty should not be equivalent",
         emptyFunc1.isEquivalentTo(nonEmpty));
   }
 }
