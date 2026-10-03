package com.google.javascript.jscomp;

import junit.framework.TestResult;
import org.junit.Assert;
import org.junit.Test;

public class TypeInferenceIssue669RegressionTest {

  @Test
  public void issue669ProducesNoUnexpectedWarnings() {
    TypeCheckTest test = new TypeCheckTest();
    test.setName("testIssue669");

    TestResult result = new TestResult();
    test.run(result);

    Assert.assertEquals("The Issue 669 regression test must execute once.", 1, result.runCount());
    Assert.assertTrue(
        "Issue 669 must compile without unexpected type-check warnings or errors.",
        result.wasSuccessful());
  }
}