package com.google.javascript.jscomp;

import org.junit.Test;

public class Closure79RegressionTest extends CompilerTestCase {
  private boolean useNormalize;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    if (useNormalize) {
      return new Normalize(compiler, false);
    }
    return new VarCheck(compiler);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testNormalizeProcessesOrdinaryFunctionWithoutInternalError() {
    useNormalize = true;
    testSame("function f(a) { var value = a; return value; }");
  }

  @Test
  public void testUndeclaredPropertyReceiverInExternsCreatesSyntheticDeclaration() {
    useNormalize = false;
    test("missingReceiver.property;", "", "");
    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testNestedUndeclaredPropertyReceiverInExternsCreatesSyntheticDeclaration() {
    useNormalize = false;
    test("missingReceiver.first.second;", "", "");
    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testUndeclaredVariableReferenceInExternsCreatesSyntheticDeclaration() {
    useNormalize = false;
    test("missingVariable;", "", "");
    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testUndeclaredCallInExternsCreatesSyntheticDeclaration() {
    useNormalize = false;
    test("missingFunction();", "", "");
    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testDeclaredPropertyReceiverInExternsDoesNotNeedSyntheticDeclaration() {
    useNormalize = false;
    test("var declaredReceiver; declaredReceiver.property;", "", "");
    assertFalse(compiler.hasCodeChanged());
  }
}