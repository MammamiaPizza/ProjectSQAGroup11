package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class PeepholeSubstituteAlternateSyntaxIssue925RegressionTest extends TestCase {

  public void testIssue925Optimization() throws Throwable {
    assertTrue(PeepholeSubstituteAlternateSyntax.containsUnicodeEscape("\\u2028"));
  }

@org.junit.Test
public void testContainsUnicodeEscapeIgnoresEscapedUnicodeMarker() {
  org.junit.Assert.assertFalse(
      PeepholeSubstituteAlternateSyntax.containsUnicodeEscape("\\\\u2028"));
}
}
