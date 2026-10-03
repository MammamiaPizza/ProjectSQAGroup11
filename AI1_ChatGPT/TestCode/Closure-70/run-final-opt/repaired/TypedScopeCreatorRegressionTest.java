package com.google.javascript.jscomp;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

public class TypedScopeCreatorRegressionTest {

  private void runExistingRegression(TestCase test, String methodName) {
    test.setName(methodName);
    TestResult result = new TestResult();
    test.run(result);

    Assert.assertEquals(
        methodName + " should not produce test errors",
        0,
        result.errorCount());
    Assert.assertEquals(
        methodName + " should satisfy its diagnostic assertions",
        0,
        result.failureCount());
  }

  @Test
  public void duplicateLocalVarDeclarationsPreserveExpectedLooseTypeCheckDiagnostics() {
    runExistingRegression(new LooseTypeCheckTest(), "testDuplicateLocalVarDecl");
  }

  @Test
  public void functionArgumentsAreCheckedByLooseTypeCheck() {
    runExistingRegression(new LooseTypeCheckTest(), "testFunctionArguments13");
  }
}
