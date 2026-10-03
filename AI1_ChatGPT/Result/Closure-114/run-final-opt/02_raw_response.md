package com.google.javascript.jscomp;

import org.junit.Test;

public final class NameAnalyzerAssignmentCallRegressionTest extends CompilerTestCase {

  public NameAnalyzerAssignmentCallRegressionTest() {
    super("", "");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new NameAnalyzer(compiler, true);
  }

  @Test
  public void testUnusedTopLevelAssignmentWithCallPreservesCall() {
    test(
        "var x; x = foo();",
        "foo();");
  }

  @Test
  public void testAssignmentWithCallUsedAsInitializerPreservesValue() {
    test(
        "var x; var y = (x = foo()); use(y);",
        "var y = foo(); use(y);");
  }
}