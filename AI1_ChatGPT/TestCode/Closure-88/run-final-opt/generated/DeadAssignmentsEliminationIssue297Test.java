package com.google.javascript.jscomp;

import org.junit.Test;

public class DeadAssignmentsEliminationIssue297Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new DeadAssignmentsElimination(compiler);
  }

  @Test
  public void testIssue297aNestedAssignmentIsReducedToItsValue() {
    test(
        "function f(){var a;a=a=1;return a;}",
        "function f(){var a;a=1;return a;}");
  }

  @Test
  public void testIssue297bNestedAssignmentInArithmeticIsReduced() {
    test(
        "function f(){var a;a=(a=1)+2;return a;}",
        "function f(){var a;a=1+2;return a;}");
  }

  @Test
  public void testIssue297cMultipleDeadNestedAssignmentsAreReduced() {
    test(
        "function f(){var a;a=(a=1)+(a=2);return a;}",
        "function f(){var a;a=1+2;return a;}");
  }

  @Test
  public void testIssue297dAssignmentNeededByLaterReadIsPreserved() {
    testSame("function f(){var a;a=(a=1)+a;return a;}");
  }

  @Test
  public void testIssue297eDeadAssignmentPreservesCallValue() {
    test(
        "function f(g){var a;a=(a=g())+1;return a;}",
        "function f(g){var a;a=g()+1;return a;}");
  }

  @Test
  public void testIssue297fAssignmentsInClosureScopeAreNotEliminated() {
    testSame("function f(){var a;a=1;function g(){return a;}return 0;}");
  }
}
