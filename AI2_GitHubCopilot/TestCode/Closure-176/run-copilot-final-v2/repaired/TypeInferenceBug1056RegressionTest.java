package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.LanguageMode;

/**

 - Regression tests for Closure bug 1056:
 - TypeInference should emit a warning when a property is assigned
 - to an undeclared (unknown) type.
  */
 public class TypeInferenceBug1056RegressionTest extends CompilerTypeTestCase {

  @Override protected void setUp() throws Exception {
    super.setUp();
    // Enable type checking warnings
    enableTypeCheck(CheckLevel.WARNING); }

  @Override protected CompilerOptions getOptions() {
    CompilerOptions options = super.getOptions();
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
    // Allow ES5 features for broader coverage
    options.setLanguageIn(LanguageMode.ECMASCRIPT5);
    return options; }

  @Override protected int getNumRepetitions() {
    return 1; }

  /**

 - Core scenario from issue 1056: assigning a property to a variable
 - of unknown type (declared without JSDoc). A warning should be emitted.
    */
   public void testPropertyAssignOnUndefinedVar() {
 test("var x; x.foo = 5;", "Property foo never defined on");
   }

  /**

 - Unknown type from parameter without annotation.
    */
   public void testPropertyAssignOnUnknownParameter() {
 test("function f(p) { p.bar = 6; } f;",
 "Property bar never defined on");

  }

  /**

 - Explicit all-type () variable; property assignment must warn.
    /
   public void testPropertyAssignOnAllType() {
 test("/ @type {*}
  */ var x; x.prop = 3;",
 "Property prop never defined on");

  }

  /**

 - Nested property assignment on unknown type: the outer property
 - access (x.a) should trigger the warning.
    /
   public void testNestedPropertyAssignOnUnknown() {
 test("/* @type {*}
  */ var x; x.a.b = 7;",
 "Property a never defined on");

  }

  /**

 - Assigning a property to a known closed record type that does not
 - have the property should already produce a warning (non-regression).
    /
   public void testPropertyAssignOnClosedTypeWarns() {
 test("/ @type {{a: number}}
  */ var x = {a: 1}; x.b = 2;",
 "Property b never defined on");

  }

  /**

 - Assigning a property to a union that includes unknown/undeclared
 - and a concrete type should also warn (because the unknown part
 - dominates or triggers the check).
    */
   public void testPropertyAssignOnUnionWithUnknown() {
 // cond ? {} : unknown creates a union (Object, ?) – effectively ?
 test("var x = (function(){})(); x.prop = 9;",
 "Property prop never defined on");

  }

  /**

 - Normal case: assigning a property to an open Object type should
 - NOT produce a warning (prevents false positives).
    /
   public void testPropertyAssignOnObjectTypeNoWarning() {
 testNoWarning("/* @type {Object}
  */ var x; x.foo = 5;");
   }

  private void testNoWarning(String js) {
    test(js); }

  /**

 - Multiple assignments to the same unknown variable – the warning
 - should appear for each distinct property.
    */
   public void testMultiplePropertyAssignments() {
 test("var x; x.p1 = 1; x.p2 = 2;",
 new String[]{"Property p1 never defined on",
              "Property p2 never defined on"});

  }

  /**

 - Property assignment inside a conditional branch; the unknown
 - type flows through and should trigger the warning regardless of
 - the branch.
    */
   public void testPropertyAssignInsideConditional() {
 test("var x; if (x) { x.flag = true; }",
 "Property flag never defined on");

  }
}
