package com.google.javascript.jscomp;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - JUnit test class for Defects4J Closure bug #96.
 - Verifies that TypeCheck properly validates function argument counts and types
 - for calls where the callee has @param annotations.
  */
 public class TypeCheckBug96Test {
  private void test(String js, DiagnosticType expectedWarning) {
  Compiler compiler = new Compiler();
  CompilerOptions options = new CompilerOptions();
  options.setCodingConvention(new DefaultCodingConvention());
  compiler.initOptions(options);
  JSSourceFile[] externs = { JSSourceFile.fromCode("externs", "") };
  JSSourceFile[] inputs = { JSSourceFile.fromCode("code", js) };
  compiler.compile(externs, inputs, options);
  JSError[] warnings = compiler.getWarnings();
  boolean found = false;
  for (JSError w : warnings) {
      if (w.getType().equals(expectedWarning)) {
          found = true;
          break;
      }
  }
  assertTrue("Expected a warning of type " + expectedWarning, found);
  }
  private void testSame(String js) {
  Compiler compiler = new Compiler();
  CompilerOptions options = new CompilerOptions();
  options.setCodingConvention(new DefaultCodingConvention());
  compiler.initOptions(options);
  JSSourceFile[] externs = { JSSourceFile.fromCode("externs", "") };
  JSSourceFile[] inputs = { JSSourceFile.fromCode("code", js) };
  compiler.compile(externs, inputs, options);
  JSError[] warnings = compiler.getWarnings();
  assertEquals("Expected no type-check warnings", 0, warnings.length);
  }
  @Test public void testCallTooFewArgumentsWithJsDoc() {
  test(
          "/** @param {number} x @param {number} y
  */ function f(x, y) {}; f(1);",
          TypeValidator.WRONG_ARGUMENT_COUNT);
  }
  @Test public void testCallTooManyArgumentsWithJsDoc() {
  test(
          "/** @param {number} x
  */ function f(x) {}; f(1, 2);",
          TypeValidator.WRONG_ARGUMENT_COUNT);
  }
  @Test public void testCallExactArgumentCountNoWarning() {
  testSame(
          "/** @param {number} x @param {number} y
  */ function f(x, y) {}; f(1, 2);");
  }
  @Test public void    testCallWithWrongPrimitiveType() {
  test(
          "/** @param {string} x
  */ function f(x) {}; f(42);",
          TypeValidator.TYPE_MISMATCH);
  }
  @Test public void    testCallWithNullForNonNullableType() {
  test(
          "/** @param {!string} name
  */ function greet(name) {}; greet(null);",
          TypeValidator.TYPE_MISMATCH);
  }
  @Test public void testCallFunctionDefinedWithMultipleParamsMissingFirst() {
  test(
          "/** @param {number} x @param {number} y
  */ function f(x, y) {}; f();",
          TypeValidator.WRONG_ARGUMENT_COUNT);
  }
  @Test public void testCallWithUnionTypeViolation() {
  test(
          "/** @param {number|string} x
  */ function f(x) {}; f(true);",
          TypeValidator.TYPE_MISMATCH);
  }
  @Test public void testCallOnNonFunctionObject() {
  test(
          "var x = 42; x();",
          TypeCheck.NOT_CALLABLE);
  }
  @Test public void testCallConstructorDirectly() {
  test(
          "/** @constructor
  */ function Foo() {}; Foo();",
          TypeCheck.CONSTRUCTOR_NOT_CALLABLE);
  }
  @Test public void testCallWithObjectTypeArgumentMismatch() {
  test(
          "/** @param {{x: number}} o
  */ function setX(o) {}; setX(null);",
          TypeValidator.TYPE_MISMATCH);
  }
  @Test public void testCallWithCorrectInterfaceType() {
  testSame(
          "/** @interface / function I() {} " +
          "/* @param {I} i / function use(i) {} " +
          "/* @constructor @implements {I}
  */ function C() {} " +
          "use(new C());");
  }
  @Test public void testCallWithTooFewArgsOnExternsFn() {
  test(
          "parseInt();",
          TypeValidator.WRONG_ARGUMENT_COUNT);
  }

}
