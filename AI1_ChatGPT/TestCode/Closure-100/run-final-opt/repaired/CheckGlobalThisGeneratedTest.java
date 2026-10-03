package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.Assert;
import org.junit.Test;

public class CheckGlobalThisGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.ERROR);
  }

  @Test
  public void testReportsGlobalThisPropertyAccess() {
    testError("this.value;", CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInNamedFunction() {
    testError(
        "function update() { this.value; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAssignmentInNamedFunction() {
    testError(
        "function update() { this.value = 1; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInStaticMethod() {
    testError(
        "Namespace.update = function() { this.value; };",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInInnerFunction() {
    testError(
        "function outer() { function inner() { this.value; } }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAccessInAssignedInnerFunction() {
    testError(
        "function outer() { var inner = function() { this.value; }; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testConstructorFunctionIsNotReported() {
    testSame(
        "/** @constructor */ function Widget() { this.value; }");
  }

  @Test
  public void testThisAnnotatedStaticFunctionIsNotReported() {
    testSame(
        "/** @this {Object} */ Namespace.update = function() { this.value; };");
  }

  @Test
  public void testPrototypeMethodIsNotReported() {
    testSame(
        "Widget.prototype.update = function() { this.value; };");
  }
}

abstract class CompilerTestCase {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void testSame(String source) {
    Compiler compiler = compile(source);
    Assert.assertEquals(0, compiler.getErrorCount());
  }

  protected void testError(String source, DiagnosticType error) {
    Compiler compiler = compile(source);
    Assert.assertEquals("There should be one error.", 1, compiler.getErrorCount());
    Assert.assertEquals(error, compiler.getErrors()[0].type);
  }

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    Node root = compiler.parseInputs();
    getProcessor(compiler).process(null, root);
    return compiler;
  }
}
