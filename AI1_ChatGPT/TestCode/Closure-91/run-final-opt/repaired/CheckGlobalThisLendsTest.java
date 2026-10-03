package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class CheckGlobalThisLendsTest extends TestCase {

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

  private void testWarning(String source, DiagnosticType warning) {
    Compiler compiler = runCheck(source);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(1, compiler.getWarnings().length);
    assertEquals(warning, compiler.getWarnings()[0].type);
  }

  private void testSame(String source) {
    Compiler compiler = runCheck(source);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(0, compiler.getWarnings().length);
  }

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
}
