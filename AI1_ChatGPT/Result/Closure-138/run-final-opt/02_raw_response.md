package com.google.javascript.jscomp;

import junit.framework.TestCase;
import org.junit.Test;

public class Closure138RegressionTest {

  @Test
  public void testGoogIsArrayOnNullNarrowsToArray() throws Throwable {
    runLegacyTest(
        ClosureReverseAbstractInterpreterTest.class, "testGoogIsArrayOnNull");
  }

  @Test
  public void testGoogIsFunctionOnNullNarrowsToFunction() throws Throwable {
    runLegacyTest(
        ClosureReverseAbstractInterpreterTest.class, "testGoogIsFunctionOnNull");
  }

  @Test
  public void testGoogIsObjectOnNullNarrowsToObject() throws Throwable {
    runLegacyTest(
        ClosureReverseAbstractInterpreterTest.class, "testGoogIsObjectOnNull");
  }

  @Test
  public void testIssue124TypeInferenceDoesNotReportUnexpectedWarnings()
      throws Throwable {
    runLegacyTest(TypeCheckTest.class, "testIssue124");
  }

  @Test
  public void testIssue124bTypeInferencePreservesFalseOutcome() throws Throwable {
    runLegacyTest(TypeCheckTest.class, "testIssue124b");
  }

  private void runLegacyTest(
      Class<? extends TestCase> testClass, String testMethod) throws Throwable {
    TestCase test = testClass.newInstance();
    test.setName(testMethod);
    test.runBare();
  }
}