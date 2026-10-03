package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckGlobalThisGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING);
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
