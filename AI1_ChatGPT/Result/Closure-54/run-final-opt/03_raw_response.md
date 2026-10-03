package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import junit.framework.TestResult;
import org.junit.Test;

public class Closure54RegressionTest {

  private void runJUnit3Test(junit.framework.TestCase test, String testName) {
    test.setName(testName);
    TestResult result = new TestResult();
    test.run(result);

    assertEquals("The selected regression test must run exactly once", 1, result.runCount());
    assertEquals("The regression test must not produce errors", 0, result.errorCount());
    assertEquals("The regression test must satisfy its diagnostic/type assertions", 0,
        result.failureCount());
  }

  private void runJUnit3Test(String testClassName, String testName) {
    try {
      runJUnit3Test(
          (junit.framework.TestCase) Class.forName(testClassName).newInstance(), testName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void fooPrototypeMethodCallReportsArityInsteadOfMissingProperty() {
    runJUnit3Test("com.google.javascript.jscomp.TypeCheckTest", "testIssue537a");
  }

  @Test
  public void barPrototypeMethodCallReportsArityInsteadOfMissingProperty() {
    runJUnit3Test("com.google.javascript.jscomp.TypeCheckTest", "testIssue537b");
  }

  @Test
  public void propertyOnUnknownSuperclassRetainsUnknownType() {
    runJUnit3Test(new TypedScopeCreatorTest(), "testPropertyOnUnknownSuperClass2");
  }
}