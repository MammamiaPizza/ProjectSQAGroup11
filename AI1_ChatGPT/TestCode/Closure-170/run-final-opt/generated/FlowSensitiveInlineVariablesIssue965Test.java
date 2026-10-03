package com.google.javascript.jscomp;

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
