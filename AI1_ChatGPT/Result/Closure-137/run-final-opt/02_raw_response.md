package com.google.javascript.jscomp;

import org.junit.Test;

public class Closure137BugTest extends CompilerTestCase {
  private enum PassType {
    INVERTER,
    NORMALIZE
  }

  private PassType passType = PassType.INVERTER;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    if (passType == PassType.NORMALIZE) {
      return new Normalize(compiler, false);
    }
    return MakeDeclaredNamesUnique.getContextualRenameInverter(compiler);
  }

  private void testInversion(String source, String expected) {
    passType = PassType.INVERTER;
    test(source, expected);
  }

  private void testNormalization(String source, String expected) {
    passType = PassType.NORMALIZE;
    test(source, expected);
  }

  @Test
  public void testContextualInverterRestoresParameterAndReferences() {
    testInversion(
        "function f(a$$1){return a$$1;}",
        "function f(a){return a;}");
  }

  @Test
  public void testContextualInverterRestoresArgumentsName() {
    testInversion(
        "function f(arguments$$1){return arguments$$1;}",
        "function f(arguments){return arguments;}");
  }

  @Test
  public void testContextualInverterRestoresDistinctNestedBindings() {
    testInversion(
        "function f(a$$1){function g(a$$2){return a$$2;}return a$$1;}",
        "function f(a){function g(a){return a;}return a;}");
  }

  @Test
  public void testContextualInverterLeavesNonContextualDoubleDollarNameAlone() {
    testInversion(
        "function f(x$$name){return x$$name;}",
        "function f(x$$name){return x$$name;}");
  }

  @Test
  public void testNormalizeRemovesRepeatedUninitializedLocalDeclaration() {
    testNormalization(
        "function f(){var a;var a;return a;}",
        "function f(){var a;return a;}");
  }

  @Test
  public void testNormalizeConvertsInitializedDuplicateDeclarationToAssignment() {
    testNormalization(
        "function f(){var a;var a=1;return a;}",
        "function f(){var a;a=1;return a;}");
  }
}