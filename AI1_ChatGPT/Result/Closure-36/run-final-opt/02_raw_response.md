package com.google.javascript.jscomp;

import org.junit.Test;

public class InlineVariablesGeneratedTest extends CompilerTestCase {

  public InlineVariablesGeneratedTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineVariables(compiler, InlineVariables.Mode.ALL, true);
  }

  @Test
  public void testInlinesImmutableLocalIntoSingleRead() {
    test(
        "function f(){var x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesSeparateInitialization() {
    test(
        "function f(){var x;x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesImmutableLocalIntoMultipleReads() {
    test(
        "function f(){var x=1;return x+x;}",
        "function f(){return 1+1;}");
  }

  @Test
  public void testDoesNotInlineVariableWithLaterAssignment() {
    testSame("function f(){var x=1;x=2;return x;}");
  }

  @Test
  public void testDoesNotInlineReadBeforeInitialization() {
    testSame("function f(){var x;x;x=1;return x;}");
  }

  @Test
  public void testDoesNotInlineRenamePropertyFunctionVariable() {
    testSame(
        "function f(){"
            + "var JSCompiler_renameProperty='p';"
            + "return JSCompiler_renameProperty;"
            + "}");
  }

  @Test
  public void testDoesNotInlineSingletonGetterClassFunction() {
    testSame(
        "function f(){"
            + "var x=function(){};"
            + "goog.addSingletonGetter(x);"
            + "}");
  }
}