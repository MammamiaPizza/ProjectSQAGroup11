package com.google.javascript.jscomp.parsing;

import junit.framework.TestCase;
import junit.framework.TestResult;

public class IRFactorySuspiciousBlockCommentTest extends TestCase {

  public void testSuspiciousBlockCommentWarning1() {
    runParserTest("testSuspiciousBlockCommentWarning1");
  }

  public void testSuspiciousBlockCommentWarning2() {
    runParserTest("testSuspiciousBlockCommentWarning2");
  }

  public void testSuspiciousBlockCommentWarning3() {
    runParserTest("testSuspiciousBlockCommentWarning3");
  }

  public void testSuspiciousBlockCommentWarning4() {
    runParserTest("testSuspiciousBlockCommentWarning4");
  }

  public void testSuspiciousBlockCommentWarning5() {
    runParserTest("testSuspiciousBlockCommentWarning5");
  }

  private void runParserTest(String testName) {
    ParserTest parserTest = new ParserTest();
    parserTest.setName(testName);

    TestResult result = new TestResult();
    parserTest.run(result);

    assertEquals(testName + " produced unexpected errors", 0, result.errorCount());
    assertEquals(testName + " produced unexpected failures", 0, result.failureCount());
  }
}
