package com.google.javascript.jscomp;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionInjectorIssue1101RegressionTest {

  private static void assertLegacyTestPasses(TestCase test, String testName) {
    test.setName(testName);
    TestResult result = new TestResult();
    test.run(result);

    assertEquals("The requested regression test must execute exactly once: " + testName,
        1, result.runCount());
    assertEquals("Regression test failed or errored: " + testName,
        0, result.failureCount() + result.errorCount());
  }

  @Test
  public void rejectsUnsafeParameterModificationCaseA() {
    assertLegacyTestPasses(new FunctionInjectorTest(), "testIssue1101a");
  }

  @Test
  public void rejectsUnsafeParameterModificationCaseB() {
    assertLegacyTestPasses(new FunctionInjectorTest(), "testIssue1101b");
  }

  @Test
  public void preservesBehaviorForBug4944818() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testBug4944818");
  }

  @Test
  public void preservesBehaviorForDoubleInlining() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testDoubleInlining2");
  }

  @Test
  public void preservesBehaviorForIssue1101Inlining() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testIssue1101");
  }

  @Test
  public void preservesBehaviorWhenModifiedParametersRequireAliases() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testInlineIfParametersModified8");
  }

  @Test
  public void preservesBehaviorForAdditionalModifiedParameterAliasing() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testInlineIfParametersModified9");
  }

  @Test
  public void preservesBehaviorForSupportedFunctionInlining() {
    assertLegacyTestPasses(new InlineFunctionsTest(), "testInlineFunctions6");
  }
}