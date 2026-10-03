package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TypeCheckPercentAccountingTest extends CompilerTestCase {
  private TypeCheck typeCheck;

  @Before
  public void initializeCompilerTestCase() throws Exception {
    setUp();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    typeCheck =
        new TypeCheck(
            compiler,
            new SemanticReverseAbstractInterpreter(
                compiler.getCodingConvention(), compiler.getTypeRegistry()),
            compiler.getTypeRegistry(),
            CheckLevel.WARNING,
            CheckLevel.OFF);
    return typeCheck;
  }

  @Test
  public void typedPercentIsCompleteForDotPropertyAssignment() {
    testSame("/** @type {Object} */ var x = {}; x.foo = 1;");

    assertEquals(100.0, typeCheck.getTypedPercent(), 0.0);
  }

  @Test
  public void typedPercentIsCompleteForBracketPropertyAssignment() {
    testSame("/** @type {Object} */ var x = {}; x['foo'] = 1;");

    assertEquals(100.0, typeCheck.getTypedPercent(), 0.0);
  }
}
