package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerTestCase;

/**

 - Tests for {@link CheckGlobalThis}.
  */
 public class CheckGlobalThisTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING); }

  @Override protected int getDefaultOptions() {
    return super.getDefaultOptions() | CompilerTestCase.IDEA_MODE; }

  // ------------- Trigger tests for bug 125 -------------

  @Test public void testPropertyOfMethod() {
    // Property-of-method: a function assigned to an object property should
    // still be checked for global this.  The buggy version misses this.
    testWarning("var obj={};obj.m=function(){this.x=1;}",
        CheckGlobalThis.GLOBAL_THIS); }

  @Test public void testMethod4() {
    // A function annotated with @this must be ignored.
    testSame("/** @this {Element} */ function f() { this.innerHTML = 'hello'; }"); }

  @Test public void testInterface1() {
    // A function that is part of an @interface should not trigger a warning.
    testSame("/** @interface */ function I() {}; I.prototype.m = function() { this.x = 1; };"); }

  // ------------- Basic dangerous uses (should warn) -------------

  @Test public void testPlainFunction() {
    testWarning("function f() { this.x = 1; }", CheckGlobalThis.GLOBAL_THIS); }

  @Test public void testThisOnLeftSideOfAssignment() {
    testWarning("var a = this;", CheckGlobalThis.GLOBAL_THIS); }

  @Test public void testNestedFunction() {
    // The inner function has no safe context, so this is dangerous.
    testWarning("function f() { function g() { this.y = 2; } }",
        CheckGlobalThis.GLOBAL_THIS); }

  // ------------- Safe contexts (no warning) -------------

  @Test public void testConstructor() {
    testSame("/** @constructor */ function C() { this.attr = 5; }"); }

  @Test public void testOverride() {
    testSame("/** @override */ function f() { this.x = 1; }"); }

  @Test public void testAtThisAnnotationOnAssignment() {
    testSame("/** @this {Object} */ var f = function() { this.x = 1; };"); }

  @Test public void testPrototypeMethod() {
    // Methods on prototypes are considered safe.
    testSame("x.prototype.m = function() { this.x = 1; };"); }

  @Test public void testObjectLiteralMethod() {
    // Object-literal methods do not have a this annotation point,
    // so the pass must not flag them.
    testSame("var a = { m: function() { this.x = 1; } };"); }

  @Test public void testPlainThisWithoutPropertyAccess() {
    // A plain this that is neither left-hand of assign nor property access
    // is not considered unsafe by the original contract.
    testSame("function f() { return this; }"); }
}