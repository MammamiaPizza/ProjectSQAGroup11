package com.google.javascript.jscomp;

import org.junit.Test;

public class TypeCheckBug11RegressionTest {

  @Test
  public void getprop4ReportsTheExpectedWarning() throws Throwable {
    TypeCheckTest test = new TypeCheckTest();
    test.setName("testGetprop4");
    test.runBare();
  }

  @Test
  public void issue810ReportsTheExpectedWarning() throws Throwable {
    TypeCheckTest test = new TypeCheckTest();
    test.setName("testIssue810");
    test.runBare();
  }
}
