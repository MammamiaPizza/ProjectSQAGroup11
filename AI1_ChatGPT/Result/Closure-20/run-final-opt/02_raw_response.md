package com.google.javascript.jscomp;

import org.junit.Test;

public class PeepholeSubstituteAlternateSyntaxBug759Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeSubstituteAlternateSyntax(false));
  }

  @Override
  protected int getNumRepetitions() {
    return 2;
  }

  @Test
  public void testFoldsStringWithImmutableLiteralArguments() {
    test("String('value')", "'' + 'value'");
    test("String(0)", "'' + 0");
    test("String(true)", "'' + true");
    test("String(null)", "'' + null");
  }

  @Test
  public void testDoesNotFoldStringWithVariableArgument() {
    testSame("String(value)");
  }

  @Test
  public void testDoesNotFoldStringWithCallResultArgument() {
    testSame("String(getValue())");
  }

  @Test
  public void testDoesNotFoldStringWithoutArguments() {
    testSame("String()");
  }

  @Test
  public void testDoesNotDropAdditionalStringArguments() {
    testSame("String('value', sideEffect())");
  }
}