package com.google.javascript.jscomp;

import org.junit.Test;

public class RemoveConstantExpressionsRegressionTest extends CompilerTestCase {

  public RemoveConstantExpressionsRegressionTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveConstantExpressions(compiler);
  }

  @Test
  public void testCallInDiscardedAdditionIsPreserved() {
    test("foo() + 1;", "foo();");
  }

  @Test
  public void testNewInDiscardedAdditionIsPreserved() {
    test("new foo() + 1;", "new foo();");
  }

  @Test
  public void testMultipleSideEffectingCallsAreRetainedInOrder() {
    test("1 + foo() + bar();", "foo();bar();");
  }

  @Test
  public void testPureExpressionStatementIsRemoved() {
    test("1 + 2;", "");
  }

  @Test
  public void testCallArgumentsNeededByCallAreNotRemoved() {
    testSame("foo(bar());");
  }

  @Test
  public void testDirectNewExpressionIsNotRemoved() {
    testSame("new Foo();");
  }
}