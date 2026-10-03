package com.google.javascript.jscomp;

import org.junit.Test;

public class DeadAssignmentsEliminationBug384Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new DeadAssignmentsElimination(compiler);
  }

  @Test
  public void testEliminatesOverwrittenAssignment() {
    test(
        "function f(){var x;x=1;x=2;return x;}",
        "function f(){var x;x=2;return x;}");
  }

  @Test
  public void testPreservesAssignmentWhenLaterOperandReadsVariable() {
    testSame("function f(){var x;return (x=1)+x;}");
  }

  @Test
  public void testEliminatesDeadAssignmentInTrueHookBranch() {
    test(
        "function f(a){var x;return a?x=1:x;}",
        "function f(a){var x;return a?1:x;}");
  }

  @Test
  public void testEliminatesDeadAssignmentWithSideEffectInTrueHookBranch() {
    test(
        "function f(a){var x;return a?x=g():x;}",
        "function f(a){var x;return a?g():x;}");
  }

  @Test
  public void testEliminatesDeadAssignmentInsideCommaExpressionInTrueHookBranch() {
    test(
        "function f(a){var x;return a?(x=1,2):x;}",
        "function f(a){var x;return a?(1,2):x;}");
  }

  @Test
  public void testEliminatesDeadIncrementButPreservesExpressionValue() {
    test(
        "function f(a){var x;return a?x++:x;}",
        "function f(a){var x;return a?x:x;}");
  }
}
