package com.google.javascript.jscomp;

import org.junit.Test;

public class RemoveUnusedVarsRegressionTest extends CompilerTestCase {

  public RemoveUnusedVarsRegressionTest() {
    super("");
    enableNormalize();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedVars(compiler, true, false, true);
  }

  @Test
  public void testRemovesUnusedMiddleParameterAndCorrespondingArgument() {
    test(
        "function f(a, b, c) { return a + c; } f(1, 2, 3);",
        "function f(a, c) { return a + c; } f(1, 3);");
  }

  @Test
  public void testRemovesUnusedTrailingParameterAndLiteralArgument() {
    test(
        "function f(a, b) { return a; } f(1, 2);",
        "function f(a) { return a; } f(1);");
  }

  @Test
  public void testKeepsReferencedParameters() {
    testSame("function f(a, b) { return a + b; } f(1, 2);");
  }

  @Test
  public void testDoesNotChangeMiddleParameterWhenFunctionHasNonCallReference() {
    testSame(
        "function f(a, b, c) { return a + c; }"
            + "var alias = f;"
            + "alias(4, 5, 6);"
            + "f(1, 2, 3);");
  }

  @Test
  public void testPreservesSideEffectingExtraArgumentWhenTrailingParameterIsRemoved() {
    test(
        "function f(a, b) { return a; } f(1, sideEffect());",
        "function f(a) { return a; } f(1, sideEffect());");
  }
}