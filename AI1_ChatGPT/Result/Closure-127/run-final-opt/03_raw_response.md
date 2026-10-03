package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;

abstract class CompilerTestCase extends TestCase {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", input)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    getProcessor(compiler).process(compiler.getExternsRoot(), root);

    Compiler expectedCompiler = new Compiler();
    expectedCompiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("expected", expected)),
        new CompilerOptions());
    Node expectedRoot = expectedCompiler.parseInputs();

    assertEquals(expectedCompiler.toSource(expectedRoot), compiler.toSource(root));
  }

  protected void testSame(String source) {
    test(source, source);
  }
}

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