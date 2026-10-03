package com.google.javascript.jscomp;

import org.junit.Test;

public final class FunctionRewriterRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FunctionRewriter(compiler);
  }

  @Test
  public void testReducibleConstantFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function() { return 1; })();");
  }

  @Test
  public void testReducibleEmptyFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function() {})();");
  }

  @Test
  public void testReducibleIdentityFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function(value) { return value; })(3);");
  }

  @Test
  public void testReducibleGetterFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function() { return this.value; })();");
  }

  @Test
  public void testReducibleSetterFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function(value) { this.value = value; })(3);");
  }

  @Test
  public void testNonReducibleFunctionUsedAsDirectCallCallee() {
    testSame("var result = (function(value) { return value + 1; })(3);");
  }

  @Test
  public void testFunctionExpressionInNonCallContext() {
    testSame("var constantFunction = function() { return 1; };");
  }

  @Test
  public void testFunctionWithMultipleStatementsIsNotReduced() {
    testSame("var result = (function() { var value = 1; return value; })();");
  }
}
