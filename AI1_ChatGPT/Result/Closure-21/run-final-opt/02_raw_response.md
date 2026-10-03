package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckSideEffectsBug21Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckSideEffects(compiler, CheckLevel.WARNING, false);
  }

  @Test
  public void testComparisonResultWithCallIsReported() {
    testWarning("x == foo();", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testStandaloneLiteralIsReported() {
    testWarning("1;", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testLikelyMissingStringConcatenationIsReported() {
    testWarning(
        "var s = 'this string is '\n"
            + "'continued on the next line';",
        CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testUselessForInitializerIsReported() {
    testWarning("for (1;;) {}", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testCallStatementIsNotReported() {
    testSame("foo();");
  }

  @Test
  public void testAssignmentStatementIsNotReported() {
    testSame("x = foo();");
  }

  @Test
  public void testEmptyStatementIsNotReported() {
    testSame(";");
  }
}