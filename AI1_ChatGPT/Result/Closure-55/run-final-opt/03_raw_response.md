package com.google.javascript.jscomp;

import java.util.Collections;

import org.junit.Test;

import com.google.javascript.rhino.Node;

import static org.junit.Assert.assertEquals;

public final class FunctionRewriterRegressionTest {

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    Node root = compiler.parseInputs();
    String before = root.toStringTree();

    Node externs = root.getFirstChild();
    Node jsRoot = externs.getNext();
    new FunctionRewriter(compiler).process(externs, jsRoot);

    assertEquals(before, root.toStringTree());
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