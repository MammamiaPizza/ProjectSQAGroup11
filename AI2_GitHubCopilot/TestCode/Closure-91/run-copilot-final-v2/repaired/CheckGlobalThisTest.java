package com.google.javascript.jscomp;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.CheckGlobalThis;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.DiagnosticType;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.CompilerTestCase;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**

 - Tests for {@link CheckGlobalThis}.
  */
 public class CheckGlobalThisTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING); }

  public void testLendsAnnotationOnFunctionExpression() {
    // Function expression with @lends should not trigger GLOBAL_THIS warning
    testSame(
        "var obj = /** @lends {Object} */ function() { this.property = 0; };"); }

  public void testLendsAnnotationOnFunctionAssignedToVar() {
    // @lends attached to a variable holding a function expression
    testSame(
        "/** @lends {SomeType} */ var fn = function() { this.x = 1; };"); }

  public void testLendsAnnotationOnObjectMethod() {
    // Object literal method with @lends
    testSame(
        "var obj = { /** @lends {MyType} */ method: function() { this.y = 0; } };"); }

  public void testLendsAnnotationOnFunctionWithGetter() {
    // this used in a getter pattern inside function with @lends
    testSame(
        "var o = /** @lends {P} */ function() { return this.a; };"); }

  public void testLendsAnnotationOnNestedAssignmentFunction() {
    // @lends on a function assigned to a deeply nested property
    testSame(
        "some.ns.md = /** @lends {Z} */ function() { this.foo = 0; };"); }

  public void testNoLendsShouldWarnGlobalThis() {
    // Function without any annotation using this
    testError(
        "var f = function() { this.bad = 1; };",
        CheckGlobalThis.GLOBAL_THIS); }

  public void testNoLendsShouldWarnPropertyAccess() {
    // this.prop without annotation
    testError(
        "function test() { this.x; }",
        CheckGlobalThis.GLOBAL_THIS); }

  public void testConstructorAnnotationPreventsWarning() {
    // @constructor prevents warning on this
    testSame(
        "/** @constructor */ function Ctor() { this.name = ''; }"); }

  public void testThisTypeAnnotationPreventsWarning() {
    // @this annotation
    testSame(
        "/** @this {Element} */ function handler() { this.attribute = null; }"); }

  public void testOverrideAnnotationPreventsWarning() {
    // @override... this usage should be okay
    testSame(
        "/** @override */ proto.method = function() { this.value = 5; };"); }

  public void testInterfaceAnnotationPreventsWarning() {
    // @interface
    testSame(
        "/** @interface */ function Interface() { this.color = 0; }"); }

  public void testLendsAnnotationOnNonFunctionIsIgnored() {
    // @lends on a non-function node, still warns inside function
    testError(
        "/** @lends {Foo} */ var config = { action: function() { this.broken = 0; } };",
        CheckGlobalThis.GLOBAL_THIS); }
}
