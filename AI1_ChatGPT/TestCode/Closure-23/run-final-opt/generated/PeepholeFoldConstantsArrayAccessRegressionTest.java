package com.google.javascript.jscomp;

import org.junit.Test;

public class PeepholeFoldConstantsArrayAccessRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants(false));
  }

  @Test
  public void testArrayAccessAtZeroFoldsFirstElementWithoutError() {
    test("([10, 20])[0]", "10");
  }

  @Test
  public void testArrayAccessAtLastValidIndexFoldsCorrectElement() {
    test("([10, 20])[1]", "20");
  }

  @Test
  public void testArrayAccessToHoleFoldsToUndefined() {
    test("([10, , 30])[1]", "void 0");
  }

  @Test
  public void testArrayAccessWithNonNumericIndexIsNotFolded() {
    testSame("([10, 20])['0']");
  }

  @Test
  public void testArrayAccessAssignmentTargetIsNotFolded() {
    testSame("([10])[0] += 1");
  }

  @Test
  public void testEmptyArrayAccessReportsOutOfBounds() {
    testSame("([])[0]", PeepholeFoldConstants.INDEX_OUT_OF_BOUNDS_ERROR);
  }

  @Test
  public void testFractionalArrayIndexReportsInvalidIndex() {
    testSame("([10])[0.5]", PeepholeFoldConstants.INVALID_GETELEM_INDEX_ERROR);
  }
}
