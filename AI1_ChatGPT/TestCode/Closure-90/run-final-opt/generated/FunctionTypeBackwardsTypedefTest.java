package com.google.javascript.jscomp;

import junit.framework.TestResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionTypeBackwardsTypedefTest {

  @Test
  public void testBackwardsTypedefUseAcceptsCompatibleArgument() {
    runExistingTypeCheckTest("testBackwardsTypedefUse8");
  }

  @Test
  public void testBackwardsTypedefUseRejectsIncompatibleArgument() {
    runExistingTypeCheckTest("testBackwardsTypedefUse9");
  }

  private void runExistingTypeCheckTest(String testName) {
    TypeCheckTest test = new TypeCheckTest();
    test.setName(testName);

    TestResult result = new TestResult();
    test.run(result);

    assertEquals(testName + " produced errors", 0, result.errorCount());
    assertEquals(testName + " produced failures", 0, result.failureCount());
  }
}
