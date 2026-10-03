package com.google.javascript.jscomp;

public class InlineVariablesArgumentsAliasTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineVariables(
        compiler, InlineVariables.Mode.LOCALS_ONLY, false);
  }

  public void testOrdinaryLocalConstantIsInlined() {
    test(
        "function f(){var x=1;return x;}",
        "function f(){return 1;}");
  }

  public void testArgumentsAliasModifiedInOuterFunctionIsNotInlined() {
    testSame(
        "function f(){var x=arguments;x[0]=1;return x[0];}");
  }

  public void testArgumentsAliasModifiedInInnerFunctionIsNotInlined() {
    testSame(
        "function f(){var x=arguments;function g(){x[0]=1;}return x[0];}");
  }

  public void testArgumentsAliasModifiedBeforeInnerReadIsNotInlined() {
    testSame(
        "function f(){var x=arguments;x[0]=1;function g(){return x[0];}return g();}");
  }

  public void testArgumentsAliasModifiedAfterInnerReadIsNotInlined() {
    testSame(
        "function f(){var x=arguments;function g(){return x[0];}x[0]=1;return g();}");
  }

  public void testArgumentsAliasEscapesThroughInnerReturnIsNotInlined() {
    testSame(
        "function f(){var x=arguments;function g(){return x;}return g();}");
  }

  public void testArgumentsAliasEscapesThroughInnerCallIsNotInlined() {
    testSame(
        "function f(){var x=arguments;function g(){sink(x);}g();}");
  }

  public void testArgumentsAliasEscapesThroughInnerAssignmentIsNotInlined() {
    testSame(
        "function f(){var x=arguments;function g(){var y=x;return y[0];}return g();}");
  }

  public void testArgumentsAliasEscapesAndIsLaterReadIsNotInlined() {
    testSame(
        "function f(){var x=arguments;escape(x);function g(){return x[0];}return g();}");
  }
}