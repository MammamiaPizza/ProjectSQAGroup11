package com.google.javascript.jscomp;

 import com.google.common.collect.ImmutableList;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.DiagnosticGroups;
 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.Node;

 import junit.framework.TestCase;

 import java.util.List;

 /**
  * Tests for {@link TypedScopeCreator} that target the bug causing missing
  * "inconsistent return type" warnings (Issue 688).
  * <p>
  * The tests focus on function return type inconsistency detection and enum
  * initializer validation, which depend on the correct operation of
  * {@code TypedScopeCreator.createScope}, {@code defineObjectLiteral},
  * {@code handleFunctionInputs}, {@code declareArguments}, and
  * {@code FirstOrderFunctionAnalyzer.process}.
  */
 public final class TypedScopeCreatorTest extends TestCase {
   private Compiler compiler;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     // Enable the inconsistent-return warning that is missing in the buggy version
     options.setWarningLevel(DiagnosticGroups.INCONSISTENT_RETURN, CheckLevel.WARNING);
     compiler.initOptions(options);
   }

   private void compile(String js) {
     List<SourceFile> externs = ImmutableList.of(
         SourceFile.fromCode("externs", ""));
     List<SourceFile> inputs = ImmutableList.of(
         SourceFile.fromCode("testcode", js));
     compiler.compile(externs, inputs, compiler.getOptions());
   }

   private void assertContainsWarning(String js, String expectedSubstring) {
     compile(js);
     JSError[] warnings = compiler.getResult().warnings;
     for (JSError w : warnings) {
       if (w.description.contains(expectedSubstring)) {
         return;
       }
     }
     fail("Expected warning containing: " + expectedSubstring);
   }

   private void assertNoWarning(String js, String avoidedSubstring) {
     compile(js);
     JSError[] warnings = compiler.getResult().warnings;
     for (JSError w : warnings) {
       if (w.description.contains(avoidedSubstring)) {
         fail("Unexpected warning: " + w.description);
       }
     }
     // success – no forbidden warning
   }

   private void assertContainsError(String js, String expectedSubstring) {
     compile(js);
     JSError[] errors = compiler.getResult().errors;
     for (JSError e : errors) {
       if (e.description.contains(expectedSubstring)) {
         return;
       }
     }
     fail("Expected error containing: " + expectedSubstring);
   }

   // ---------- Normal positive cases ----------
   public void testInconsistentReturnNumberString() {
     assertContainsWarning(
         "function f() { if (true) return 1; return 'a'; }",
         "inconsistent return type"
     );
   }

   public void testInconsistentReturnStringBoolean() {
     assertContainsWarning(
         "function f() { if (true) return 'hello'; return true; }",
         "inconsistent return type"
     );
   }

   public void testInconsistentReturnWithUndefined() {
     assertContainsWarning(
         "function f() { if (true) return; return 1; }",
         "inconsistent return type"
     );
   }

   // ---------- Consistent / negative cases ----------
   public void testConsistentReturnNumber() {
     assertNoWarning(
         "function f() { if (true) return 1; return 2; }",
         "inconsistent return type"
     );
   }

   public void testFunctionWithNoReturn() {
     assertNoWarning(
         "function f() { var x = 1; }",
         "inconsistent return type"
     );
   }

   public void testConsistentSingleReturn() {
     assertNoWarning(
         "function f() { return 42; }",
         "inconsistent return type"
     );
   }

   // ---------- Enum-related boundary cases (key to Issue 688) ----------
   public void testInconsistentReturnWithCorrectEnum() {
     // Correctly defined enum – inconsistency detection should work.
     assertContainsWarning(
         "/** @enum {number}\n*/ var MyEnum = {A:1}; "
             + "function f() { if (true) return MyEnum.A; return 'a'; }",
         "inconsistent return type"
     );
   }

   public void testInconsistentReturnWithEnumNonObjectLiteral() {
     // Regression for Issue 688: enum initialised via non-object-literal
     // should still allow the compiler to detect an inconsistent return.
     assertContainsWarning(
         "var init = {A:1}; /** @enum {number}\n*/ var MyEnum = init; "
             + "function f() { if (true) return MyEnum.A; return 'a'; }",
         "inconsistent return type"
     );
   }

   public void testEnumInitializerNotObjectLiteralError() {
     // The method TypedScopeCreator#defineObjectLiteral must reject
     // enum initializers that are not object literals.
     assertContainsError(
         "/** @enum {number}\n*/ var MyEnum = 42;",
         "enum initializer must be an object literal or an enum"
     );
   }

   public void testEnumInitializerVariableError() {
     assertContainsError(
         "var x = 5; /** @enum {number}\n*/ var MyEnum = x;",
         "enum initializer must be an object literal or an enum"
     );
   }

   public void testEnumObjectLiteralPasses() {
     // Object-literal initializer must NOT trigger the error.
     assertNoWarning(
         "/** @enum {number} */ var MyEnum = {A:1, B:2};",
         "enum initializer must be an object literal or an enum"
     );
     // Also check that no error is present.
     compile("/** @enum {number}\n*/ var MyEnum = {A:1, B:2};");
     for (JSError e : compiler.getResult().errors) {
       if (e.description.contains("enum initializer")) {
         fail("Unexpected error for valid enum: " + e.description);
       }
     }
   }
 }
