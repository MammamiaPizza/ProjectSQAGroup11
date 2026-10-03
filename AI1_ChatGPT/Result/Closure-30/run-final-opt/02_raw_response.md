package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestResult;
import org.junit.Test;

public class FlowSensitiveInlineVariablesRegressionTest {

  @Test
  public void testInliningAcrossSideEffectPreservesReachingDefinitionSafety() {
    runExistingRegression("testInlineAcrossSideEffect1");
  }

  @Test
  public void testInliningAcrossNoSideEffectUsesUniqueReachingDefinition() {
    runExistingRegression("testCanInlineAcrossNoSideEffect");
  }

  @Test
  public void testIssue698DependencyAndEscapingRegression() {
    runExistingRegression("testIssue698");
  }

  private void runExistingRegression(String testName) {
    FlowSensitiveInlineVariablesTest regression =
        new FlowSensitiveInlineVariablesTest();
    regression.setName(testName);

    TestResult result = new TestResult();
    regression.run(result);

    assertEquals("The requested regression test must run exactly once.", 1, result.runCount());
    assertTrue(
        "Flow-sensitive inlining regression failed for " + testName,
        result.wasSuccessful());
  }
}