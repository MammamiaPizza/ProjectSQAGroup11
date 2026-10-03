package com.google.javascript.jscomp;

import org.junit.Test;

public class NormalizeRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new Normalize(compiler, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testMovesNamedFunctionDeclarationsBeforeStatements() {
    test(
        "function outer(){a();function first(){}b();function second(){}}",
        "function outer(){function first(){}function second(){}a();b();}");
  }

  @Test
  public void testMovesNamedFunctionsInsideNestedFunctionBodies() {
    test(
        "function outer(){function inner(){x();function late(){}}y();}",
        "function outer(){function inner(){function late(){}x();}y();}");
  }

  @Test
  public void testFunctionDeclarationAndUninitializedVarDoNotConflict() {
    test(
        "function outer(){var f;function f(){return 1;}}",
        "function outer(){function f(){return 1;}}");
  }

  @Test
  public void testFunctionDeclarationAndInitializedVarBecomeAssignment() {
    test(
        "function outer(){var f=0;function f(){return 1;}}",
        "function outer(){function f(){return 1;}f=0;}");
  }

  @Test
  public void testSplitsMultipleVarDeclarations() {
    test(
        "function f(){var a=1,b=2,c;}",
        "function f(){var a=1;var b=2;var c;}");
  }

  @Test
  public void testDuplicateInitializedVarIsReplacedWithAssignment() {
    test(
        "function f(){var a;var a=1;}",
        "function f(){var a;a=1;}");
  }

  @Test
  public void testDuplicateUninitializedVarIsRemoved() {
    test(
        "function f(){var a;var a;}",
        "function f(){var a;}");
  }

  @Test
  public void testNormalizesLabelBodyToBlock() {
    test("label: work();", "label:{work();}");
  }

  @Test
  public void testExtractsForVarInitializer() {
    test(
        "function f(){for(var i=0;i<2;i++){work(i);}}",
        "function f(){var i=0;for(;i<2;i++){work(i);}}");
  }
}