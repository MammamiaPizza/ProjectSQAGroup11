package com.google.javascript.jscomp;

 import com.google.javascript.rhino.JSDocInfo;
 import com.google.javascript.rhino.JSTypeExpression;
 import com.google.javascript.rhino.SimpleErrorReporter;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.InstanceObjectType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.ObjectType;

 import junit.framework.TestCase;

 /**
  * Tests that target the bug described in Closure issue 274 where
  * {@link FunctionTypeBuilder#isFunctionTypeDeclaration} fails to recognise
  * {@code @typedef} as a function type declaration.  This causes
  * type-check warnings for backwards typedef usage (Defects4J trigger tests
  * {@code TypeCheckTest#testBackwardsTypedefUse8} and
  * {@code TypeCheckTest#testBackwardsTypedefUse9}).
  */
 public class FunctionTypeBuilderBugTest extends TestCase {

   private JSTypeRegistry registry;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     registry = new JSTypeRegistry(new SimpleErrorReporter());
   }

   /*
    * isFunctionTypeDeclaration tests -------------------------------------------------
    */

   /** The method must return {@code true} when {@code @typedef} is present. */
   public void testIsFunctionTypeDeclarationTypedefReturnsTrue() {
     JSDocInfo info = new JSDocInfo(false);
     info.setTypedef(true);
     assertTrue(
         "isFunctionTypeDeclaration should accept @typedef",
         FunctionTypeBuilder.isFunctionTypeDeclaration(info));
   }

   /** The method must return {@code true} when {@code @constructor} is present. */
   public void testIsFunctionTypeDeclarationConstructorReturnsTrue() {
     JSDocInfo info = new JSDocInfo(false);
     info.setConstructor(true);
     assertTrue(FunctionTypeBuilder.isFunctionTypeDeclaration(info));
   }

   /** The method must return {@code true} when {@code @interface} is present. */
   public void testIsFunctionTypeDeclarationInterfaceReturnsTrue() {
     JSDocInfo info = new JSDocInfo(false);
     info.setInterface(true);
     assertTrue(FunctionTypeBuilder.isFunctionTypeDeclaration(info));
   }

   /** No annotations → not a function type declaration. */
   public void testIsFunctionTypeDeclarationNoAnnotationReturnsFalse() {
     JSDocInfo info = new JSDocInfo(false);
     assertFalse(FunctionTypeBuilder.isFunctionTypeDeclaration(info));
   }

   /** Only unrelated annotations (e.g. {@code @param}, {@code @return}) → false. */
   public void testIsFunctionTypeDeclarationOnlyOtherTagsReturnsFalse() {
     JSDocInfo info = new JSDocInfo(false);
     info.setReturnType(new JSTypeExpression(null, "string"));
     info.setParameterType("x", new JSTypeExpression(null, "number"));
     assertFalse(FunctionTypeBuilder.isFunctionTypeDeclaration(info));
   }

   /*
    * FunctionType kind tests ---------------------------------------------------------
    */

   /** A function created as a constructor reports as a constructor. */
   public void testFunctionTypeConstructorKind() {
     FunctionType ctor = registry.createConstructor("C", null);
     assertTrue("isConstructor should be true", ctor.isConstructor());
     assertFalse("isInterface should be false", ctor.isInterface());
   }

   /** An ordinary (non-constructor) function reports as neither constructor nor interface. */
   public void testFunctionTypeOrdinaryKind() {
     FunctionType func = registry.createFunctionType("g");
     assertFalse("isConstructor should be false", func.isConstructor());
     assertFalse("isInterface should be false", func.isInterface());
   }

   /** A constructor has a non-null instance type. */
   public void testConstructorHasInstanceType() {
     FunctionType ctor = registry.createConstructor("C", null);
     ObjectType instanceType = ctor.getInstanceType();
     assertNotNull("A constructor must have an instance type", instanceType);
     assertTrue("Instance type should be an InstanceObjectType",
         instanceType instanceof InstanceObjectType);
   }

   /** An ordinary function's {@code typeOfThis} defaults to the unknown type (non-null). */
   public void testOrdinaryFunctionTypeOfThisIsUnknown() {
     FunctionType func = registry.createFunctionType("g");
     JSType typeOfThis = func.getTypeOfThis();
     assertNotNull("typeOfThis should not be null for ordinary function", typeOfThis);
     assertEquals(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), typeOfThis);
   }

   /**
    * {@code setPrototypeBasedOn} should wire the implicit prototype without error
    * on a constructor (sanity check that base-type handling works).
    */
   public void testConstructorPrototypeBasedOn() {
     FunctionType ctor = registry.createConstructor("C", null);
     ObjectType base = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
     ctor.setPrototypeBasedOn(base);
     assertEquals("Implicit prototype should be the set base type",
         base, ctor.getImplicitPrototype());
   }
 }