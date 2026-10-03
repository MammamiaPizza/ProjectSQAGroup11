package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import junit.framework.TestCase;

public class InlineVariablesArgumentsAliasTest extends TestCase {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineVariables(
        compiler, InlineVariables.Mode.LOCALS_ONLY, false);
  }

  private void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());

    Node root = compiler.parseSyntheticCode(source);
    Node expectedRoot = compiler.parseSyntheticCode(expected);

    getProcessor(compiler).process(null, root);

    assertEquals(compiler.toSource(expectedRoot), compiler.toSource(root));
  }

  private void testSame(String source) {
    test(source, source);
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
