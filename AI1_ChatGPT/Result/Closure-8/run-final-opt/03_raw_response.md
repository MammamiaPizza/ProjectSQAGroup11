package com.google.javascript.jscomp;

import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

public class CollapseVariableDeclarationsGeneratedTest extends CompilerTestCase {

  public CollapseVariableDeclarationsGeneratedTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseVariableDeclarations(compiler);
  }

  @Test
  public void testCollapsesAdjacentInitializedDeclarations() {
    test(
        "var a = 1; var b = 2; var c = 3;",
        "var a = 1, b = 2, c = 3;");
  }

  @Test
  public void testCollapsesAdjacentDeclarationsInsideFunction() {
    test(
        "function f() { var a; var b = 2; var c; }",
        "function f() { var a, b = 2, c; }");
  }

  @Test
  public void testDoesNotCollapseAcrossOrdinaryExpression() {
    testSame("var a = 1; foo(); var b = 2;");
  }

  @Test
  public void testCollapsesAssignmentToDeclaredVariable() {
    test(
        "var a = 1; a = 2;",
        "/** @suppress {duplicate} */ var a = 1, a = 2;");
  }

  @Test
  public void testDoesNotRedeclareStubVariable() {
    testSame("var a; a = 1;");
  }

  @Test
  public void testStubVariablePreventsCollapsePastItsAssignment() {
    testSame("var a; a = 1; var b = 2;");
  }

  @Test
  public void testDoesNotTreatPropertyAssignmentAsRedeclarable() {
    testSame("var a = 1; a.x = 2; var b = 3;");
  }

  @Test
  public void testDoesNotCollapseVariableDeclarationsInIfBranches() {
    testSame("if (flag) var a = 1; else var b = 2;");
  }

  @Test
  public void testDoesNotCollapseAcrossNestedFunctionScope() {
    testSame("var a = 1; function f() { var b = 2; } var c = 3;");
  }

  @Test
  public void testStubAndInitializedDeclarationCanStillBeCombined() {
    test(
        "var a; var b = 1;",
        "var a, b = 1;");
  }
}

abstract class CompilerTestCase extends TestCase {
  CompilerTestCase(String externs) {
  }

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    assertEquals(compile(expected, false), compile(input, true));
  }

  protected void testSame(String input) {
    test(input, input);
  }

  private String compile(String source, boolean collapseDeclarations) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.collapseVariableDeclarations = collapseDeclarations;
    List<SourceFile> inputs =
        Collections.singletonList(SourceFile.fromCode("input", source));
    Result result =
        compiler.compile(Collections.<SourceFile>emptyList(), inputs, options);
    assertEquals(0, result.errors.length);
    return compiler.toSource();
  }
}