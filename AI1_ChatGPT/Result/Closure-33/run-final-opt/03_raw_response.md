package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

public class PrototypeObjectTypeIssue700Test {

  @Test
  public void testIssue700ProducesNoTypeCheckWarnings() throws Exception {
    TestCase test =
        (TestCase) Class.forName("com.google.javascript.jscomp.TypeCheckTest").newInstance();
    test.setName("testIssue700");

    TestResult result = new TestResult();
    test.run(result);

    assertEquals(1, result.runCount());
    assertEquals(0, result.failureCount());
    assertEquals(0, result.errorCount());
  }
}