package com.google.javascript.jscomp;

import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Closure79RegressionTest {
  private Compiler createCompiler(String externs, String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.singletonList(SourceFile.fromCode("externs.js", externs)),
        Collections.singletonList(SourceFile.fromCode("source.js", source)),
        new CompilerOptions());
    compiler.parseInputs();
    return compiler;
  }

  @Test
  public void testNormalizeProcessesOrdinaryFunctionWithoutInternalError() {
    String source = "function f(a) { var value = a; return value; }";
    Compiler expectedCompiler = createCompiler("", source);
    Compiler compiler = createCompiler("", source);

    new Normalize(compiler, false).process(
        compiler.getExternsRoot(), compiler.getJsRoot());

    assertEquals(expectedCompiler.toSource(), compiler.toSource());
  }

  @Test
  public void testUndeclaredPropertyReceiverInExternsCreatesSyntheticDeclaration() {
    Compiler compiler = createCompiler("missingReceiver.property;", "");

    new VarCheck(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testNestedUndeclaredPropertyReceiverInExternsCreatesSyntheticDeclaration() {
    Compiler compiler = createCompiler("missingReceiver.first.second;", "");

    new VarCheck(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testUndeclaredVariableReferenceInExternsCreatesSyntheticDeclaration() {
    Compiler compiler = createCompiler("missingVariable;", "");

    new VarCheck(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testUndeclaredCallInExternsCreatesSyntheticDeclaration() {
    Compiler compiler = createCompiler("missingFunction();", "");

    new VarCheck(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertTrue(compiler.hasCodeChanged());
  }

  @Test
  public void testDeclaredPropertyReceiverInExternsDoesNotNeedSyntheticDeclaration() {
    Compiler compiler = createCompiler(
        "var declaredReceiver; declaredReceiver.property;", "");

    new VarCheck(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertFalse(compiler.hasCodeChanged());
  }
}
