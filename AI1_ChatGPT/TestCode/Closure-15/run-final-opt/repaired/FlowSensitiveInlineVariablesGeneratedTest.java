package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FlowSensitiveInlineVariablesGeneratedTest {

  private Node parse(String code, Compiler compiler) {
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", code)),
        new CompilerOptions());
    compiler.parseInputs();
    return compiler.getJsRoot();
  }

  private void test(String input, String expected) {
    Compiler compiler = new Compiler();
    Node root = parse(input, compiler);
    new FlowSensitiveInlineVariables(compiler).process(
        compiler.getExternsRoot(), root);

    Compiler expectedCompiler = new Compiler();
    Node expectedRoot = parse(expected, expectedCompiler);
    assertTrue(root.isEquivalentTo(expectedRoot));
  }

  private void testSame(String input) {
    test(input, input);
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
