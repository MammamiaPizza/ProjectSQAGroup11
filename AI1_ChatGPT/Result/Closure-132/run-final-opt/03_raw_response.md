package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class PeepholeSubstituteAlternateSyntaxIssue925RegressionTest extends TestCase {

  public void testIssue925Optimization() throws Throwable {
    assertTrue(PeepholeSubstituteAlternateSyntax.containsUnicodeEscape("\\u2028"));
  }
}