package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class CheckGlobalThisIssue182RegressionTest {

  private Compiler runCheck(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    new CheckGlobalThis(compiler, CheckLevel.WARNING).process(null, root);
    return compiler;
  }

  private void testWarning(String source) {
    Compiler compiler = runCheck(source);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(1, compiler.getWarnings().length);
    assertEquals(CheckGlobalThis.GLOBAL_THIS, compiler.getWarnings()[0].type);
  }

  private void testSame(String source) {
    Compiler compiler = runCheck(source);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(0, compiler.getWarnings().length);
  }

  @Test
  public void testObjectLiteralFunctionAssignmentReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  method: function() {"
            + "    this.value = 1;"
            + "  }"
            + "};");
  }

  @Test
  public void testObjectLiteralFunctionPropertyAccessReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  method: function() {"
            + "    this.value;"
            + "  }"
            + "};");
  }

  @Test
  public void testQuotedObjectLiteralFunctionPropertyReportsGlobalThis() {
    testWarning(
        "var object = {"
            + "  'method-name': function() {"
            + "    this.value = 1;"
            + "  }"
            + "};");
  }

  @Test
  public void testStandaloneThisExpressionIsNotReported() {
    testSame(
        "function f() {"
            + "  this;"
            + "}");
  }

  @Test
  public void testConstructorThisUseIsNotReported() {
    testSame(
        "/** @constructor */"
            + "function C() {"
            + "  this.value = 1;"
            + "}");
  }

  @Test
  public void testPrototypeMethodThisUseIsNotReported() {
    testSame(
        "C.prototype.method = function() {"
            + "  this.value = 1;"
            + "};");
  }

  @Test
  public void testThisAnnotatedFunctionIsNotReported() {
    testSame(
        "/** @this {Object} */"
            + "function f() {"
            + "  this.value = 1;"
            + "}");
  }
}
