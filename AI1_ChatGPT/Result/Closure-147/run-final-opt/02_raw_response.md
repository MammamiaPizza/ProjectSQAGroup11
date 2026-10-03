package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckGlobalThisIssue182RegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING);
  }

  @Test
  public void testObjectLiteralFunctionAssignmentReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  method: function() {"
            + "    this.value = 1;"
            + "  }"
            + "};",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testObjectLiteralFunctionPropertyAccessReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  method: function() {"
            + "    this.value;"
            + "  }"
            + "};",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testQuotedObjectLiteralFunctionPropertyReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  'method-name': function() {"
            + "    this.value = 1;"
            + "  }"
            + "};",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testStandaloneThisExpressionIsNotReported() {
    testSame(
        "function f() {"
            + "  this;"
            + "}");
  }

  @Test
  public void testConstructorThisUseIsNotReported() {
    testSame(
        "/** @constructor */"
            + "function C() {"
            + "  this.value = 1;"
            + "}");
  }

  @Test
  public void testPrototypeMethodThisUseIsNotReported() {
    testSame(
        "C.prototype.method = function() {"
            + "  this.value = 1;"
            + "};");
  }

  @Test
  public void testThisAnnotatedFunctionIsNotReported() {
    testSame(
        "/** @this {Object} */"
            + "function f() {"
            + "  this.value = 1;"
            + "}");
  }
}