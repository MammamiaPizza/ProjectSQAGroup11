package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;

public class FlowSensitiveInlineVariablesIssue965Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler);
  }

  public void testDoesNotInlineValueUsedBeforeAssignmentInHookTrueBranch() {
    testSame(
        "function f(c) {"
            + "  var x = 1;"
            + "  return c ? (x = 2) : x;"
            + "}");
  }

  public void testDoesNotInlineValueUsedBeforeAssignmentInHookFalseBranch() {
    testSame(
        "function f(c) {"
            + "  var x = 1;"
            + "  return c ? x : (x = 2);"
            + "}");
  }

  public void testDoesNotInlineHookConditionWhenBranchAssignsVariable() {
    testSame(
        "function f(c) {"
            + "  var x = 1;"
            + "  return x ? (x = c) : 0;"
            + "}");
  }

  public void testDoesNotInlineHookConditionWhenBothBranchesAssignVariable() {
    testSame(
        "function f(c) {"
            + "  var x = 1;"
            + "  return x ? (x = c) : (x = 2);"
            + "}");
  }

  public void testDoesNotMoveSideEffectingInitializerPastHookAssignment() {
    testSame(
        "function f(c) {"
            + "  var x = sideEffect();"
            + "  return c ? x : (x = 2);"
            + "}");
  }

  public void testInlinesSimpleSingleDefinitionSingleUseVariable() {
    test(
        "function f() {"
            + "  var x = 1;"
            + "  return x;"
            + "}",
        "function f() {"
            + "  return 1;"
            + "}");
  }

  public void testDoesNotInlineVariableWithMultipleHookUses() {
    testSame(
        "function f(c) {"
            + "  var x = 1;"
            + "  return c ? x : x;"
            + "}");
  }

  public void testDoesNotInlineUninitializedVariable() {
    testSame(
        "function f() {"
            + "  var x;"
            + "  return x;"
            + "}");
  }
}

abstract class CompilerTestCase extends TestCase {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void testSame(String source) {
    test(source, source);
  }

  protected void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node actualRoot = compiler.parseInputs();
    getProcessor(compiler).process(null, actualRoot);

    Compiler expectedCompiler = new Compiler();
    expectedCompiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", expected)),
        new CompilerOptions());
    Node expectedRoot = expectedCompiler.parseInputs();

    assertTrue(expected, expectedRoot.isEquivalentTo(actualRoot));
  }
}