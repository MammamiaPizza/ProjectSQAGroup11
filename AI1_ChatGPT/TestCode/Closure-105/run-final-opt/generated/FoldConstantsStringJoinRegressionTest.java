package com.google.javascript.jscomp;

import org.junit.Test;

public class FoldConstantsStringJoinRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FoldConstants(compiler);
  }

  @Test
  public void testFoldsAdjacentStringSuffixAfterUnknownValue() {
    test(
        "var x = a + 'b' + 'c';",
        "var x = a + 'bc';");
  }

  @Test
  public void testFoldsLongAdjacentStringSuffixAfterUnknownValue() {
    test(
        "var x = a + 'x' + 'y' + 'z';",
        "var x = a + 'xyz';");
  }

  @Test
  public void testFoldsStringAndNumericLiteralSuffixAfterUnknownValue() {
    test(
        "var x = a + 'x' + 1;",
        "var x = a + 'x1';");
  }

  @Test
  public void testFoldsEmptyStringSuffix() {
    test(
        "var x = a + '' + 'x';",
        "var x = a + 'x';");
  }

  @Test
  public void testDoesNotFoldWhenPriorAdditionMayBeNumeric() {
    testSame("var x = a + 1 + 'x';");
  }

  @Test
  public void testFoldsFullyConstantStringAddition() {
    test(
        "var x = 'a' + 'b' + 'c';",
        "var x = 'abc';");
  }
}
