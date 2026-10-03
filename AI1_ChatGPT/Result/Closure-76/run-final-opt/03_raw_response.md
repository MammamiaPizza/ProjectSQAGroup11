package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class DeadAssignmentsEliminationBug384Test {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new DeadAssignmentsElimination(compiler);
  }

  private void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    getProcessor(compiler).process(null, root);
    assertEquals(expected, compiler.toSource(root));
  }

  private void testSame(String source) {
    test(source, source);
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