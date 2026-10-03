package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class UnreachableCodeEliminationGeneratedTest {

  private boolean removeNoOpStatements;

  private void test(String input, String expected) {
    assertEquals(compile(expected, false), compile(input, true));
  }

  private void testSame(String input) {
    test(input, input);
  }

  private String compile(String source, boolean runPass) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    Node root = compiler.parseInputs();
    if (runPass) {
      new UnreachableCodeElimination(compiler, removeNoOpStatements)
          .process(root.getFirstChild(), root.getLastChild());
    }
    return compiler.toSource();
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