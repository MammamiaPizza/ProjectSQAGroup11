package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class TypedScopeCreatorTest extends TestCase {

   private static final String ENUM_INIT_WARNING =
       "enum initializer must be an object literal or an enum";

   private Compiler compiler;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
   }

   private void checkEnumWarning(String js, boolean expectWarning) {
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);
     compiler.compile(
         SourceFile.fromCode("externs", ""),
         SourceFile.fromCode("test", js));

     JSError[] warnings = compiler.getWarnings();
     boolean found = false;
     for (JSError w : warnings) {
       if (w.description.contains(ENUM_INIT_WARNING)) {
         found = true;
         break;
       }
     }

     if (expectWarning) {
       assertTrue("Expected enum init warning for: " + js, found);
     } else {
       assertFalse("Unexpected enum init warning for: " + js, found);
     }
   }

   public void testEnumWithObjectLiteralInit() {
     checkEnumWarning("/** @enum {number} */ var E = {A: 1, B: 2};", false);
   }

   public void testEnumWithAnotherEnum() {
     checkEnumWarning(
         "/** @enum {number} */ var E1 = {A: 1};"
         + "/** @enum {number} */ var E2 = E1;",
         false);
   }

   public void testEnumWithVariableInit() {
     checkEnumWarning(
         "var x = {A: 1};/** @enum {number} */ var E = x;",
         true);
   }

   public void testEnumWithNumberInit() {
     checkEnumWarning("/** @enum {number} */ var E = 42;", true);
   }

   public void testEnumWithStringInit() {
     checkEnumWarning("/** @enum {string} */ var E = 'hello';", true);
   }

   public void testEnumWithNullInit() {
     checkEnumWarning("/** @enum {number} */ var E = null;", true);
   }

   public void testEnumWithBooleanInit() {
     checkEnumWarning("/** @enum {boolean} */ var E = true;", true);
   }

   public void testEnumWithArrayInit() {
     checkEnumWarning("/** @enum {number} */ var E = [1, 2, 3];", true);
   }

   public void testEnumWithFunctionCallInit() {
     checkEnumWarning(
         "/** @return {Object} */ function f() { return {}; }"
         + "/** @enum {number} */ var E = f();",
         true);
   }

   public void testEnumWithUnaryOpInit() {
     checkEnumWarning("/** @enum {number} */ var E = -1;", true);
   }

   public void testEnumWithTernaryInit() {
     checkEnumWarning(
         "/** @enum {number} */ var E = true ? {A:1} : {B:2};",
         true);
   }

   public void testEnumWithNoInitializer() {
     checkEnumWarning("/** @enum {number} */ var E;", true);
   }
 }