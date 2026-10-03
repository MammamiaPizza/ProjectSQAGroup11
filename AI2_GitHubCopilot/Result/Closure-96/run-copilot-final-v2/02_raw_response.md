package com.google.javascript.jscomp;

import org.junit.Test;

/**

 - JUnit test class for Defects4J Closure bug #96.
 - Verifies that TypeCheck properly validates function argument counts and types
 - for calls where the callee has @param annotations.
  */
 public class TypeCheckBug96Test extends TypeCheckTest {

  @Test public void testCallTooFewArgumentsWithJsDoc() {
    // Expect WRONG_ARGUMENT_COUNT when calling with fewer args than declared.
    test(
        "/** @param {number} x @param {number} y */ function f(x, y) {}; f(1);",
        TypeValidator.WRONG_ARGUMENT_COUNT); }

  @Test public void testCallTooManyArgumentsWithJsDoc() {
    // Expect WRONG_ARGUMENT_COUNT when calling with more args than declared.
    test(
        "/** @param {number} x */ function f(x) {}; f(1, 2);",
        TypeValidator.WRONG_ARGUMENT_COUNT); }

  @Test public void testCallExactArgumentCountNoWarning() {
    // Should compile without any warning.
    testSame(
        "/** @param {number} x @param {number} y */ function f(x, y) {}; f(1, 2);"); }

  @Test public void testCallWithWrongPrimitiveType() {
    // Number where string is expected → TYPE_MISMATCH warning.
    test(
        "/** @param {string} x */ function f(x) {}; f(42);",
        TypeValidator.TYPE_MISMATCH); }

  @Test public void testCallWithNullForNonNullableType() {
    // null cannot be passed for a non-nullable string.
    test(
        "/** @param {!string} name */ function greet(name) {}; greet(null);",
        TypeValidator.TYPE_MISMATCH); }

  @Test public void testCallFunctionDefinedWithMultipleParamsMissingFirst() {
    // Call with completely wrong argument (undefined type) – still wrong count.
    test(
        "/** @param {number} x @param {number} y */ function f(x, y) {}; f();",
        TypeValidator.WRONG_ARGUMENT_COUNT); }

  @Test public void testCallWithUnionTypeViolation() {
    // Parameter expects number|string, boolean is not allowed.
    test(
        "/** @param {number|string} x */ function f(x) {}; f(true);",
        TypeValidator.TYPE_MISMATCH); }

  @Test public void testCallOnNonFunctionObject() {
    // Calling a non-function should produce NOT_CALLABLE.
    test(
        "var x = 42; x();",
        TypeCheck.NOT_CALLABLE); }

  @Test public void testCallConstructorDirectly() {
    // Non-native constructor called without 'new'.
    test(
        "/** @constructor */ function Foo() {}; Foo();",
        TypeCheck.CONSTRUCTOR_NOT_CALLABLE); }

  @Test public void testCallWithObjectTypeArgumentMismatch() {
    // Parameter expects an object with a specific shape; null is invalid.
    test(
        "/** @param {{x: number}} o */ function setX(o) {}; setX(null);",
        TypeValidator.TYPE_MISMATCH); }

  @Test public void testCallWithCorrectInterfaceType() {
    // A simple interface-based argument check that should not warn.
    testSame(
        "/** @interface / function I() {} " +
        "/* @param {I} i / function use(i) {} " +
        "/* @constructor @implements {I} */ function C() {} " +
        "use(new C());"); }

  @Test public void testCallWithTooFewArgsOnExternsFn() {
    // Even built-in functions should generate warnings when misused.
    test(
        "parseInt();",
        TypeValidator.WRONG_ARGUMENT_COUNT); }
}