package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.Test;

public class CheckGlobalThisGeneratedTest {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING);
  }

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    getProcessor(compiler).process(null, compiler.parseInputs());
    assertEquals(0, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
  }

  private void testWarning(String source, DiagnosticType warning) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    getProcessor(compiler).process(null, compiler.parseInputs());
    assertEquals(0, compiler.getErrorCount());
    assertEquals(1, compiler.getWarningCount());
    assertEquals(warning, compiler.getWarnings()[0].type);
  }

  @Test
  public void testPropertyOfPrototypeMethodStillReportsGlobalThis() {
    testWarning(
        "function Foo() {}"
            + "Foo.prototype.method.property = this;",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testBracketedPrototypeMethodDoesNotReportThis() {
    testSame(
        "function Foo() {}"
            + "Foo['prototype'].method = function() { this.value = 1; };");
  }

  @Test
  public void testInterfaceFunctionDoesNotReportThis() {
    testSame(
        "/** @interface */"
            + "function Foo() { this.value = 1; }");
  }

  @Test
  public void testOrdinaryFunctionPropertyAccessReportsThis() {
    testWarning(
        "function f() { this.value = 1; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testConstructorDoesNotReportThis() {
    testSame(
        "/** @constructor */"
            + "function Foo() { this.value = 1; }");
  }

  @Test
  public void testDirectPrototypeMethodDoesNotReportThis() {
    testSame(
        "function Foo() {}"
            + "Foo.prototype.method = function() { this.value = 1; };");
  }

  @Test
  public void testGlobalThisPropertyAccessReportsThis() {
    testWarning("this.value = 1;", CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testBareGlobalThisDoesNotReportThis() {
    testSame("this;");
  }
}