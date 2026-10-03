package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import org.junit.Test;

public class FlowSensitiveInlineVariablesRegressionTest {

  @Test
  public void testInliningAcrossSideEffectPreservesReachingDefinitionSafety() {
    assertTransformed(
        "function f() { var x = foo(); bar(); return x; }",
        "function f() { var x = foo(); bar(); return x; }");
  }

  @Test
  public void testInliningAcrossNoSideEffectUsesUniqueReachingDefinition() {
    assertTransformed(
        "function f() { var x = 1; return x; }",
        "function f() { return 1; }");
  }

  @Test
  public void testIssue698DependencyAndEscapingRegression() {
    assertTransformed(
        "function f(a) { var x = a; a = 1; return x; }",
        "function f(a) { var x = a; a = 1; return x; }");
  }

  private void assertTransformed(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());

    Node root = compiler.parseSyntheticCode(source);
    new FlowSensitiveInlineVariables(compiler).process(null, root);

    Node expectedRoot = compiler.parseSyntheticCode(expected);
    assertEquals(expectedRoot.toStringTree(), root.toStringTree());
  }
}