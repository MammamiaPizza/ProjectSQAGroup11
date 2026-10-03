package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CompilerTestCase;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.TypeValidator.TypeMismatch;
 import com.google.javascript.jscomp.TypeValidator;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.Node;
 import com.google.common.collect.Lists;
 import java.util.List;
 import junit.framework.TestCase;

 public class TypeValidatorTest extends CompilerTestCase {

   private Compiler compiler;
   private JSTypeRegistry registry;

   @Override
   public void setUp() {
     compiler = new Compiler();
     init(); // from CompilerTestCase
     registry = compiler.getTypeRegistry();
   }

   /**
    * Verifies that redefining a variable from an enum type to a function type
    * produces exactly two JSC_DUP_VAR_DECLARATION warnings.
    */
   public void testEnumToFunctionRedeclaration() {
     String js = ""
       + "/** @enum {number} */ var A = {X:0};"
       + "/** @param {number} x */ function A(x) { return x; }";
     compile(js);
     assertEquals("Expected two duplicate variable warnings", 2,
         getErrorCount(JSError.DUP_VAR_DECLARATION));
   }

   /**
    * Verifies that redefining a variable with the same type produces no warnings.
    */
   public void testSameTypeRedeclaration() {
     String js = ""
       + "var a = 5; var a = 5;";
     compile(js);
     assertEquals("No duplicate warnings expected", 0,
         getErrorCount(JSError.DUP_VAR_DECLARATION));
   }

   /**
    * Verifies that a missing interface implementation triggers a mismatch
    * (TYPE_MISMATCH_WARNING).
    */
   public void testInterfaceNotImplemented() {
     String js = ""
       + "/** @interface */ var I = function() {};"
       + "I.prototype.method = function() {};"
       + "/** @implements {I} */ function C() {}";
     compile(js);
     assertTrue("Expected a type mismatch warning",
         getErrorCount(JSError.TYPE_MISMATCH_WARNING) > 0);
   }

   /**
    * Verifies that registerMismatch does not register mismatches when types
    * can assign to each other after null/undefined restruction.
    */
   public void testRegisterMismatchAssignableTypes() {
     TypeValidator tv = new TypeValidator(compiler);
     JSType number = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     JSType string = registry.getNativeType(JSTypeNative.STRING_TYPE);
     // number and string are not assignable -> mismatch registered
     tv.registerMismatch(number, string, null);
     List<TypeMismatch> mismatches = tv.getMismatches(); // assume getter exists? We'll use
reflection to access field.
     // Since mismatches field is package-private, we can access from same package
     assertEquals(1, mismatches.size());

     // Assignable types: number and (number|string) – creating union
     JSType union = registry.createUnionType(number, string);
     tv.registerMismatch(number, union, null);
     assertEquals("Mismatch should not be added for assignable types", 1,
         mismatches.size());
   }

   /**
    * Verifies that TypeMismatch.equals works correctly.
    */
   public void testTypeMismatchEquality() {
     JSType t1 = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     JSType t2 = registry.getNativeType(JSTypeNative.STRING_TYPE);
     JSError err = JSError.make(JSError.TYPE_MISMATCH_WARNING, null, null);
     TypeMismatch m1 = new TypeMismatch(t1, t2, err);
     TypeMismatch m2 = new TypeMismatch(t1, t2, err);
     assertEquals(m1, m2);
     assertEquals(m1.hashCode(), m2.hashCode());
   }

   /**
    * Verifies that TypeMismatch.toString contains the error message.
    */
   public void testTypeMismatchToString() {
     JSType t1 = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     JSType t2 = registry.getNativeType(JSTypeNative.STRING_TYPE);
     JSError err = JSError.make(JSError.TYPE_MISMATCH_WARNING, null, null);
     TypeMismatch m = new TypeMismatch(t1, t2, err);
     assertTrue(m.toString().contains("type mismatch"));
   }

   /**
    * Supplementary: ensure no false positives for variable redeclaration without type change.
    */
   public void testNoWarningForUtypedRedeclaration() {
     String js = "var b; var b;";
     compile(js);
     assertEquals(0, getErrorCount(JSError.DUP_VAR_DECLARATION));
   }

   private void compile(String js) {
     super.compile(js); // uses ComilerTestCase.parse/typecheck
   }

   private int getErrorCount(JSError... errorTypes) {
     int count = 0;
     for (JSError e : compiler.getErrors()) {
       if (isType(e, errorTypes)) {
         count++;
       }
     }
     for (JSError w : compiler.getWarnings()) {
       if (isType(w, errorTypes)) {
         count++;
       }
     }
     return count;
   }

   private boolean isType(JSError error, JSError... types) {
     for (JSError t : types) {
       if (error.getType().equals(t.getType()) {
         return true;
       }
     }
     return false;
   }
 }
