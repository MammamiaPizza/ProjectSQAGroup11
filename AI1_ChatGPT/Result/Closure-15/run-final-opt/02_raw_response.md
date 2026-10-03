package com.google.javascript.jscomp;

import org.junit.Test;

public class FlowSensitiveInlineVariablesGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler);
  }

  @Test
  public void testInlinesSingleLiteralUse() {
    test(
        "function f() { var x = 1; return x; }",
        "function f() { return 1; }");
  }

  @Test
  public void testDoesNotInlineForInAssignmentTarget() {
    testSame(
        "function f() { var x = 1; for (x in y) {} }");
  }

  @Test
  public void testDoesNotInlineValueUsedTwice() {
    testSame(
        "function f() { var x = 1; return x + x; }");
  }

  @Test
  public void testDoesNotInlineUseInsideLoop() {
    testSame(
        "function f() { var x = 1; while (g()) { h(x); } }");
  }

  @Test
  public void testDoesNotInlineSideEffectingInitializer() {
    testSame(
        "function f() { var x = g(); return x; }");
  }

  @Test
  public void testDoesNotInlineParameter() {
    testSame(
        "function f(x) { return x; }");
  }
}