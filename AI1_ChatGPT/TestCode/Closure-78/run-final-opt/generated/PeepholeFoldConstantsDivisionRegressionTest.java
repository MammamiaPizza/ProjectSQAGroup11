package com.google.javascript.jscomp;

public class PeepholeFoldConstantsDivisionRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants());
  }

  public void testDivisionWithZeroResultAndNonZeroDivisor() {
    test("var result = 0 / 8;", "var result = 0;");
  }

  public void testNonzeroConstantDivisionFoldsToInteger() {
    test("var result = 8 / 2;", "var result = 4;");
  }

  public void testFractionalConstantDivisionFolds() {
    test("var result = 1 / 2;", "var result = 0.5;");
  }
}
