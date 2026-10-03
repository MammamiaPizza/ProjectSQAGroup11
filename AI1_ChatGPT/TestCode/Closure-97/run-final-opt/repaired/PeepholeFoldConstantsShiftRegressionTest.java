package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import org.junit.Test;

public class PeepholeFoldConstantsShiftRegressionTest {

  private void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());
    Node root = compiler.parseTestCode(source);
    new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants()).process(null, root);
    assertEquals(expected + ";", compiler.toSource(root));
  }

  @Test
  public void testFoldsOrdinaryLeftAndSignedRightShifts() {
    test("10 << 2", "40");
    test("-8 >> 2", "-2");
  }

  @Test
  public void testFoldsOrdinaryUnsignedRightShift() {
    test("8 >>> 2", "2");
    test("-1 >>> 1", "2147483647");
  }

  @Test
  public void testUnsignedRightShiftByZeroPreservesUnsignedValue() {
    test("-1 >>> 0", "4294967295");
  }

  @Test
  public void testUnsignedRightShiftByZeroForMinimumIntPreservesUnsignedValue() {
    test("-2147483648 >>> 0", "2147483648");
  }

  @Test
  public void testFoldsMaximumValidShiftCount() {
    test("1 << 31", "-2147483648");
    test("-1 >> 31", "-1");
    test("-1 >>> 31", "1");
  }

  @Test
  public void testFoldsZeroLeftOperandAtMaximumShiftCount() {
    test("0 << 31", "0");
    test("0 >> 31", "0");
    test("0 >>> 31", "0");
  }
}
