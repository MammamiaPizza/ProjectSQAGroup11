package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class FunctionInjectorRegressionTest extends TestCase {

  private void runInlineFunctionsTrigger(String testName) throws Throwable {
    InlineFunctionsTest test = new InlineFunctionsTest();
    test.setName(testName);
    test.runBare();
  }

  public void testBug4944818() throws Throwable {
    runInlineFunctionsTrigger("testBug4944818");
  }

  public void testDoubleInlining1() throws Throwable {
    runInlineFunctionsTrigger("testDoubleInlining1");
  }

  public void testNoInlineIfParametersModified8() throws Throwable {
    runInlineFunctionsTrigger("testNoInlineIfParametersModified8");
  }

  public void testNoInlineIfParametersModified9() throws Throwable {
    runInlineFunctionsTrigger("testNoInlineIfParametersModified9");
  }

  public void testInlineFunctions6() throws Throwable {
    runInlineFunctionsTrigger("testInlineFunctions6");
  }
}
