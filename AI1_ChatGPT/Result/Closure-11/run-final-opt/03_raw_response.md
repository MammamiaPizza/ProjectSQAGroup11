package com.google.javascript.jscomp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;

public class TypeCheckBug11RegressionTest {

  @Test
  public void getprop4ReportsTheExpectedWarning() throws Throwable {
    runTest("testGetprop4");
  }

  @Test
  public void issue810ReportsTheExpectedWarning() throws Throwable {
    runTest("testIssue810");
  }

  private void runTest(String testName) throws Throwable {
    try {
      Class<?> testClass =
          Class.forName("com.google.javascript.jscomp.TypeCheckTest");
      Object test = testClass.newInstance();
      Method setName = testClass.getMethod("setName", String.class);
      Method runBare = testClass.getMethod("runBare");
      setName.invoke(test, testName);
      runBare.invoke(test);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }
}