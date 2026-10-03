package com.google.javascript.jscomp;

import org.junit.Test;

public class PeepholeFoldConstantsGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants());
  }

  @Override
  protected int getNumRepetitions() {
    return 2;
  }

  @Test
  public void testFoldsFirstArrayElement() {
    test("[0, 1][0]", "0");
  }

  @Test
  public void testFoldsLastArrayElement() {
    test("[0, 1][1]", "1");
  }

  @Test
  public void testFoldsArrayElementAfterHole() {
    test("[0, , 2][2]", "2");
  }

  @Test
  public void testDoesNotFoldAssignmentTargetPastCurrentArrayLength() {
    testSame("[0][1] = 1");
  }

  @Test
  public void testDoesNotFoldCompoundAssignmentTargetPastCurrentArrayLength() {
    testSame("[0][1] += 1");
  }

  @Test
  public void testDoesNotFoldAssignmentTargetAtExistingArrayIndex() {
    testSame("[0][0] = 1");
  }
}
