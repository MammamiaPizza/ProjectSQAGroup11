package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckSideEffectsGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckSideEffects(compiler, CheckLevel.WARNING, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testStandaloneNameProducesWarning() {
    testWarning("var x; x;", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testUnusedEqualityWithCallProducesWarning() {
    testWarning(
        "var x; x == foo();",
        CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testStandaloneStringProducesWarning() {
    testWarning(
        "'continued on the next line';",
        CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testStandaloneNumberProducesWarning() {
    testWarning("0;", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testCallExpressionIsNotReportedAsUseless() {
    testSame("foo();");
  }

  @Test
  public void testEmptyStatementsAreAllowed() {
    testSame(";;");
  }

  @Test
  public void testIndirectEvalCalleeCommaExpressionIsPreserved() {
    testSame("(0, eval)('x');");
  }
}