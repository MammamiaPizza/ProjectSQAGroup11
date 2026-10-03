package com.google.javascript.jscomp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InlineFunctionsGeneratedTest {

  private CompilerPass getProcessor(Compiler compiler) {
    return new InlineFunctions(
        compiler,
        compiler.getUniqueNameIdSupplier(),
        true,
        true,
        true);
  }

  private void testSame(String source) {
    test(source, source);
  }

  private void test(String source, String expected) {
    assertEquals(compile(expected, false), compile(source, true));
  }

  private String compile(String source, boolean inline) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.compile(new String[] {""}, new String[] {source}, options);
    if (inline) {
      getProcessor(compiler).process(
          compiler.getExternsRoot(), compiler.getJsRoot());
    }
    return compiler.toSource();
  }

  @Test
  public void testIssue423RecursiveFunctionIsNotInlined() {
    testSame("function x() { x(); }");
  }

  @Test
  public void testRecursiveReturnCallIsNotInlined() {
    testSame("function f() { return f(); }");
  }

  @Test
  public void testFunctionReferencingThisIsNotInlined() {
    testSame("function f() { return this.x; } var result = f();");
  }

  @Test
  public void testFunctionWithInnerFunctionIsNotInlined() {
    testSame("function f() { function g() {} return 1; } var result = f();");
  }

  @Test
  public void testFunctionUsedAsAliasIsNotInlined() {
    testSame("function f() { return 1; } var alias = f; var result = alias();");
  }

  @Test
  public void testFunctionPassedAsValueIsNotInlined() {
    testSame("function f() { return 1; } consume(f); var result = f();");
  }

  @Test
  public void testSimpleNamedFunctionIsInlinedAndRemoved() {
    test("function f() { return 1; } var result = f();", "var result = 1;");
  }

  @Test
  public void testFunctionVariableIsInlinedAndRemoved() {
    test("var f = function() { return 1; }; var result = f();", "var result = 1;");
  }

  @Test
  public void testImmediatelyInvokedFunctionExpressionIsInlined() {
    test("var result = (function() { return 1; })();", "var result = 1;");
  }

  @Test
  public void testProgramWithoutFunctionsIsUnchanged() {
    testSame("var value = 1;");
  }
}