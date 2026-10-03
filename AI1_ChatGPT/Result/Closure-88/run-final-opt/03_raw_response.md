package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.Test;

public class DeadAssignmentsEliminationIssue297Test {

  private String process(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", source)),
        new CompilerOptions());
    compiler.parseInputs();
    new DeadAssignmentsElimination(compiler)
        .process(compiler.getExternsRoot(), compiler.getJsRoot());
    return compiler.toSource();
  }

  private String print(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", source)),
        new CompilerOptions());
    compiler.parseInputs();
    return compiler.toSource();
  }

  private void test(String source, String expected) {
    assertEquals(print(expected), process(source));
  }

  private void testSame(String source) {
    test(source, source);
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