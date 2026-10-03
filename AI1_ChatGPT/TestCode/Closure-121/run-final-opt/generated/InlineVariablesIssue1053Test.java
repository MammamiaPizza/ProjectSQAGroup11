package com.google.javascript.jscomp;

import org.junit.Test;

public class InlineVariablesIssue1053Test extends CompilerTestCase {

  public InlineVariablesIssue1053Test() {
    super();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineVariables(
        compiler, InlineVariables.Mode.ALL, true);
  }

  @Test
  public void testExternalIssue1053AliasOfCallResultKeepsReferencedVariable() {
    test(
        "function f(){var y=g();var x=y;return x;}",
        "function f(){var y=g();return y;}");
  }

  @Test
  public void testInlinesImmutableLocalDeclaration() {
    test(
        "function f(){var x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesSeparatedDeclarationAndInitialization() {
    test(
        "function f(){var x;x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testDoesNotInlineVariableWithLaterAssignment() {
    testSame(
        "function f(){var x=1;x=2;return x;}");
  }

  @Test
  public void testDoesNotInlineVariableUsedAsLValue() {
    testSame(
        "function f(){var x=1;x++;return x;}");
  }

  @Test
  public void testDoesNotInlineUninitializedVariable() {
    testSame(
        "function f(){var x;return x;}");
  }
}
