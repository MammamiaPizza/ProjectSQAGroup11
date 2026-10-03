package com.google.javascript.jscomp;

import org.junit.Test;

public class PeepholeFoldConstantsOperandOrderTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants(false));
  }

  @Override
  protected int getNumRepetitions() {
    return 2;
  }

  @Test
  public void testUndefinedAndNullComparisonsFoldInEitherOrder() {
    test("undefined == null", "true");
    test("null == undefined", "true");
    test("undefined != null", "false");
    test("null != undefined", "false");
    test("undefined === null", "false");
    test("null !== undefined", "true");
  }

  @Test
  public void testCommutativeEqualityOperatorsMoveLiteralToRight() {
    test("1 == x", "x == 1");
    test("1 != x", "x != 1");
    test("1 === x", "x === 1");
    test("1 !== x", "x !== 1");
  }

  @Test
  public void testCommutativeEqualityOperatorsPreserveNonLiteralOrder() {
    testSame("x == y");
    testSame("x != y");
    testSame("x === y");
    testSame("x !== y");
  }

  @Test
  public void testInvertibleRelationalOperatorsMoveLiteralToRight() {
    test("1 < x", "x > 1");
    test("1 <= x", "x >= 1");
    test("1 > x", "x < 1");
    test("1 >= x", "x <= 1");
  }

  @Test
  public void testRelationalOperandReorderingIsSafeWithCallOnRight() {
    test("0 < f()", "f() > 0");
    test("0 <= f()", "f() >= 0");
    test("0 > f()", "f() < 0");
    test("0 >= f()", "f() <= 0");
  }

  @Test
  public void testConstantRelationalComparisonsFold() {
    test("0 < 1", "true");
    test("1 < 0", "false");
    test("0 <= 0", "true");
    test("1 >= 2", "false");
  }
}
