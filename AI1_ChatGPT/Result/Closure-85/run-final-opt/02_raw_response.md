package com.google.javascript.jscomp;

import org.junit.Test;

public class UnreachableCodeEliminationGeneratedTest extends CompilerTestCase {

  private boolean removeNoOpStatements;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new UnreachableCodeElimination(compiler, removeNoOpStatements);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testCascadedRemovalOfRedundantSwitchBreaks() {
    test(
        "function f(x) {"
            + "switch (x) {"
            + "case 1: break;"
            + "case 2: break;"
            + "default: break;"
            + "}"
            + "}",
        "function f(x) {"
            + "switch (x) {"
            + "case 1:"
            + "case 2:"
            + "default:"
            + "}"
            + "}");
  }

  @Test
  public void testBreakIsRetainedWhenFollowingCaseCodeWouldRun() {
    testSame(
        "function f(x) {"
            + "switch (x) {"
            + "case 1: break;"
            + "case 2: x();"
            + "}"
            + "}");
  }

  @Test
  public void testRedundantContinueAtEndOfLoopIsRemoved() {
    test(
        "function f(x) { while (x) { continue; } }",
        "function f(x) { while (x) { } }");
  }

  @Test
  public void testCodeAfterValueReturnIsRemoved() {
    test(
        "function f() { return 1; unreachable(); }",
        "function f() { return 1; }");
  }

  @Test
  public void testNoOpExpressionStatementsCanBeRemovedWhenEnabled() {
    removeNoOpStatements = true;
    try {
      test(
          "function f() { unusedName; effect(); }",
          "function f() { effect(); }");
    } finally {
      removeNoOpStatements = false;
    }
  }

  @Test
  public void testIssue311BreakThroughFinallyIsNotRemovedOrCrashing() {
    testSame(
        "function f() {"
            + "while (true) {"
            + "try { break; } finally { }"
            + "}"
            + "}");
  }
}