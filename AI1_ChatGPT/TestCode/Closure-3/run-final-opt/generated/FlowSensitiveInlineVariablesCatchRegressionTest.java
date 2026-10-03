package com.google.javascript.jscomp;

import org.junit.Test;

public class FlowSensitiveInlineVariablesCatchRegressionTest
    extends CompilerTestCase {

  public FlowSensitiveInlineVariablesCatchRegressionTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler);
  }

  @Test
  public void testDoesNotInlineGetPropAssignmentOutOfTryWithReturningCatch() {
    testSame(
        "function f(a) {"
            + "var x;"
            + "try { x = a.b; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }

  @Test
  public void testDoesNotInlineGetPropVarInitializerOutOfTryWithReturningCatch() {
    testSame(
        "function f(a) {"
            + "try { var x = a.b; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }

  @Test
  public void testDoesNotInlineGetElemAssignmentOutOfTryWithReturningCatch() {
    testSame(
        "function f(a, key) {"
            + "var x;"
            + "try { x = a[key]; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }
}
