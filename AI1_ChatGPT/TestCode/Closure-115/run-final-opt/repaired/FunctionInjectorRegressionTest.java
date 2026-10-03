package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestCase;

public class FunctionInjectorRegressionTest extends TestCase {

  private String compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    return compiler.toSource();
  }

  public void testBug4944818() {
    String output = compile(
        "function f(){return f;}window.result=f();");
    assertTrue(output.indexOf("function") >= 0);
  }

  public void testDoubleInlining1() {
    String output = compile(
        "function f(){return 1;}function g(){return f();}window.result=g();");
    assertFalse(output.indexOf("function") >= 0);
  }

  public void testNoInlineIfParametersModified8() {
    String output = compile(
        "function f(a){a=2;return a;}window.result=f(1);");
    assertTrue(output.indexOf("function") >= 0);
  }

  public void testNoInlineIfParametersModified9() {
    String output = compile(
        "function f(a){a++;return a;}window.result=f(1);");
    assertTrue(output.indexOf("function") >= 0);
  }

  public void testInlineFunctions6() {
    String output = compile(
        "function f(){return 1;}window.result=f();");
    assertFalse(output.indexOf("function") >= 0);
  }
}
