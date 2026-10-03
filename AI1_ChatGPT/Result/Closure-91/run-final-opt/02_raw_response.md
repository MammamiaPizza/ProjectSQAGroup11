package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckGlobalThisLendsTest extends CompilerTestCase {

  public CheckGlobalThisLendsTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalThis(compiler, CheckLevel.WARNING);
  }

  @Test
  public void testReportsGlobalThisPropertyAssignment() {
    testWarning("this.value = 1;", CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testReportsThisPropertyAssignmentInOrdinaryFunction() {
    testWarning(
        "function f() { this.value = 1; }",
        CheckGlobalThis.GLOBAL_THIS);
  }

  @Test
  public void testDoesNotReportThisInConstructor() {
    testSame("/** @constructor */ function Foo() { this.value = 1; }");
  }

  @Test
  public void testDoesNotReportThisInPrototypeMethod() {
    testSame("Foo.prototype.method = function() { this.value = 1; };");
  }

  @Test
  public void testDoesNotReportThisInLendsAnnotatedVarFunction() {
    testSame(
        "/** @lends {Foo.prototype} */ "
            + "var method = function() { this.value = 1; };");
  }

  @Test
  public void testDoesNotReportThisInLendsAnnotatedAssignedFunction() {
    testSame(
        "var method; "
            + "/** @lends {Foo.prototype} */ "
            + "method = function() { this.value = 1; };");
  }
}