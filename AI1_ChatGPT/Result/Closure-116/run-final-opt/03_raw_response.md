package com.google.javascript.jscomp;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionInjectorIssue1101RegressionTest {

  private static void assertLegacyTestPasses(String testClassName, String testName)
      throws Exception {
    TestCase test = (TestCase) Class.forName(testClassName).newInstance();
    test.setName(testName);
    TestResult result = new TestResult();
    test.run(result);

    assertEquals("The requested regression test must execute exactly once: " + testName,
        1, result.runCount());
    assertEquals("Regression test failed or errored: " + testName,
        0, result.failureCount() + result.errorCount());
  }

  @Test
  public void rejectsUnsafeParameterModificationCaseA() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.FunctionInjectorTest", "testIssue1101a");
  }

  @Test
  public void rejectsUnsafeParameterModificationCaseB() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.FunctionInjectorTest", "testIssue1101b");
  }

  @Test
  public void preservesBehaviorForBug4944818() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.InlineFunctionsTest", "testBug4944818");
  }

  @Test
  public void preservesBehaviorForDoubleInlining() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.InlineFunctionsTest", "testDoubleInlining2");
  }

  @Test
  public void preservesBehaviorForIssue1101Inlining() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.InlineFunctionsTest", "testIssue1101");
  }

  @Test
  public void preservesBehaviorWhenModifiedParametersRequireAliases() throws Exception {
    assertLegacyTestPasses(
        "com.google.javascript.jscomp.InlineFunctionsTest",
        "testInlineIfParametersModified8");
  }

  @Test
  public void preservesBehaviorForAdditionalModifiedParameterAliasing() throws Exception {
    assertLegacyTestPasses(
        "com.google.javascript.jscomp.InlineFunctionsTest",
        "testInlineIfParametersModified9");
  }

  @Test
  public void preservesBehaviorForSupportedFunctionInlining() throws Exception {
    assertLegacyTestPasses("com.google.javascript.jscomp.InlineFunctionsTest", "testInlineFunctions6");
  }
}