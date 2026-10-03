package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

public final class PeepholeOptimizationsPassIssue787Test {

  @Test
  public void testIssue787RegressionThroughIntegrationHarness() throws Exception {
    TestCase regression = (TestCase) Class.forName(
        "com.google.javascript.jscomp.IntegrationTest").newInstance();
    regression.setName("testIssue787");

    TestResult result = new TestResult();
    regression.run(result);

    assertEquals("The named integration test should run exactly once.", 1, result.runCount());
    assertEquals("Issue 787 must not cause an unexpected test error.", 0, result.errorCount());
    assertEquals("Issue 787 optimization output must match its regression oracle.", 0,
        result.failureCount());
  }
}
