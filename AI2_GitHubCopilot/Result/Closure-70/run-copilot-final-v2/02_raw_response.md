package com.google.javascript.jscomp;

public class TypedScopeCreatorTest extends TypeCheckTest {

  @Override protected void setUp() throws Exception {
    super.setUp(); }

  /**

 - Two local var declarations with incompatible types should produce two
 - JSC_DUP_VAR_DECLARATION warnings (one per redeclaration).
 - Bug 433: expected 2 warnings but only 1 was reported.
    */
   public void testDuplicateLocalVarDeclMultiple() {
 test("var x = 3; var x = 'str';",
 "JSC_DUP_VAR_DECLARATION",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Three local var declarations: first is fine, the next two are
 - redeclarations and should each trigger a warning.
    */
   public void testMultipleRedeclarations() {
 test("var a = 1; var a = true; var a = 'x';",
 "JSC_DUP_VAR_DECLARATION",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Redeclaration with compatible types still warrants a warning.
    */
   public void testDuplicateLocalVarSameType() {
 test("var b = 3; var b = 4;",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - A function parameter followed by a local var with a different type
 - should produce a duplicate declaration warning.
 - Bug 433: expected a warning but none was reported (testFunctionArguments13).
    */
   public void testFunctionArgumentThenLocalVar() {
 test("function f(x) { var x = 3; }",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Two function parameters with the same name are already caught
 - elsewhere, but combined with a local var redeclaration we still
 - expect a warning for the local var vs parameter.
    */
   public void testDuplicateParamWithLocalVar() {
 test("function g(x, x) { var x = 'str'; }",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Block-level var that shadows a parameter: warning expected.
    */
   public void testBlockRedeclarationAfterFunctionArg() {
 test("function h(x) { { var x = true; } }",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Inside a nested function, redeclaring the outer variable with var is
 - not a duplicate declaration because scopes differ.  No warning expected.
    */
   public void testNestedFunctionNoWarning() {
 testSame("var outer = 1; function inner() { var outer = 2; }");
   }

  /**

 - Var redeclared in a catch block.  The catch variable itself is a new scope;
 - a local var inside the catch block should still trigger a duplicate warning
 - if it reuses the same name.
    */
   public void testCatchBlockRedeclaration() {
 test("function k() { try {} catch (e) { var e = null; } }",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Global var and function-scoped var with the same name do not conflict.
    */
   public void testGlobalAndLocalVarNoConflict() {
 testSame("var x = 1; function fn() { var x = 2; }");
   }

  /**

 - Normal code without duplicate declarations produces no warnings.
    */
   public void testNoDuplicatesNormalCode() {
 testSame("function mul(a, b) { var c = a
  * b; return c; }");
   }

  /**

 - In a function, redeclaration of an explicitly typed local variable that
 - shadows a parameter should still produce a warning (the existing scope
 - must correctly record the parameter as a typed variable).
    /
   public void testTypedParamThenLocalVar() {
 test("/* @param {number} x
  */ function p(x) { var x = 'string'; }",
 "JSC_DUP_VAR_DECLARATION");

  }

  /**

 - Multiple function arguments followed by a local var with the same name;
 - only one warning for that redeclaration is expected.
    */
   public void testMultipleParamsThenLocalVar() {
 test("function q(x, y) { var x = false; }",
 "JSC_DUP_VAR_DECLARATION");

  }
}