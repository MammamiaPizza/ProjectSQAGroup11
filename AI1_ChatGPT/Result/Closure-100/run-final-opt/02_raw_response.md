package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckGlobalThisGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.ERROR);
  }

  @Test
  public void testReportsGlobalThisPropertyAccess() {
    testError("this.value;", CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInNamedFunction() {
    testError(
        "function update() { this.value; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAssignmentInNamedFunction() {
    testError(
        "function update() { this.value = 1; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInStaticMethod() {
    testError(
        "Namespace.update = function() { this.value; };",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInInnerFunction() {
    testError(
        "function outer() { function inner() { this.value; } }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInAssignedInnerFunction() {
    testError(
        "function outer() { var inner = function() { this.value; }; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testConstructorFunctionIsNotReported() {
    testSame(
        "/** @constructor */ function Widget() { this.value; }");
  }

  @Test
  public void testThisAnnotatedStaticFunctionIsNotReported() {
    testSame(
        "/** @this {Object} */ Namespace.update = function() { this.value; };");
  }

  @Test
  public void testPrototypeMethodIsNotReported() {
    testSame(
        "Widget.prototype.update = function() { this.value; };");
  }
}