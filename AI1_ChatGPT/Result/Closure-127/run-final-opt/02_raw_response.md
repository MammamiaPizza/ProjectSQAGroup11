package com.google.javascript.jscomp;

public class UnreachableCodeEliminationBug127Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new UnreachableCodeElimination(compiler, false);
  }

  public void testRemovesStatementAfterReturnWithValue() {
    test(
        "function f() { return 1; dead(); }",
        "function f() { return 1; }");
  }

  public void testPreservesReturnInsideTryFinally() {
    testSame(
        "function f() {"
            + "  try { return; } finally { cleanup(); }"
            + "  after();"
            + "}");
  }

  public void testPreservesContinueInsideTryFinally() {
    testSame(
        "function f(x) {"
            + "  while (x) {"
            + "    try { continue; } finally { cleanup(); }"
            + "    x = false;"
            + "  }"
            + "}");
  }

  public void testPreservesBreakInsideTryFinally() {
    testSame(
        "function f(x) {"
            + "  while (x) {"
            + "    try { break; } finally { cleanup(); }"
            + "    x = false;"
            + "  }"
            + "  return x;"
            + "}");
  }

  public void testPreservesBreakInsideTryFinallyInSwitch() {
    testSame(
        "function f(x) {"
            + "  switch (x) {"
            + "    case 1:"
            + "      try { break; } finally { cleanup(); }"
            + "    case 2:"
            + "      return 2;"
            + "  }"
            + "  return 3;"
            + "}");
  }

  public void testPreservesReturnInsideNestedTryFinally() {
    testSame(
        "function f(x) {"
            + "  while (x) {"
            + "    try {"
            + "      try { return 1; } finally { innerCleanup(); }"
            + "    } finally { outerCleanup(); }"
            + "  }"
            + "  return 2;"
            + "}");
  }
}