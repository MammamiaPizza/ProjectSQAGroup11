package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class PeepholeSubstituteAlternateSyntaxIssue925RegressionTest extends TestCase {

  public void testIssue925Optimization() throws Throwable {
    PeepholeSubstituteAlternateSyntaxTest regressionTest =
        new PeepholeSubstituteAlternateSyntaxTest();
    regressionTest.setName("testIssue925");
    regressionTest.runBare();
  }
}