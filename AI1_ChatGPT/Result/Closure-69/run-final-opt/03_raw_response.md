package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import org.junit.Test;

public class TypeCheckThisTypeRegressionTest {

  private static final class Diagnostics {
    final int errors;
    final int warnings;

    Diagnostics(int errors, int warnings) {
      this.errors = errors;
      this.warnings = warnings;
    }
  }

  private Diagnostics typeCheck(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();

    compiler.init(
        new SourceFile[] {SourceFile.fromCode("externs.js", "")},
        new SourceFile[] {SourceFile.fromCode("input.js", source)},
        options);
    compiler.parseInputs();

    Node root = compiler.getRoot();
    Node externs = root.getFirstChild();
    Node js = externs.getNext();

    TypeCheck typeCheck =
        new TypeCheck(
            compiler,
            new SemanticReverseAbstractInterpreter(
                compiler.getCodingConvention(), compiler.getTypeRegistry()),
            compiler.getTypeRegistry());
    typeCheck.processForTesting(externs, js);

    return new Diagnostics(compiler.getErrors().length, compiler.getWarnings().length);
  }

  @Test
  public void testThisAnnotationOnFunctionDeclarationIsChecked() {
    Diagnostics diagnostics =
        typeCheck("/** @this {string} */ function f() { return this - 1; }");

    assertEquals(0, diagnostics.errors);
    assertTrue("A string this value must not be accepted as a number", diagnostics.warnings > 0);
  }

  @Test
  public void testThisAnnotationOnAnonymousFunctionExpressionIsChecked() {
    Diagnostics diagnostics =
        typeCheck("var f = /** @this {string} */ function() { return this - 1; };");

    assertEquals(0, diagnostics.errors);
    assertTrue(
        "A @this annotation on a function expression must type the function body",
        diagnostics.warnings > 0);
  }

  @Test
  public void testThisTypeInFunctionSignatureIsCheckedInFunctionBody() {
    Diagnostics diagnostics =
        typeCheck(
            "/** @type {function(this:string)} */ "
                + "var f = function() { return this - 1; };");

    assertEquals(0, diagnostics.errors);
    assertTrue(
        "A this type supplied by a function signature must be used while checking the body",
        diagnostics.warnings > 0);
  }

  @Test
  public void testThisAnnotationOnNamedFunctionExpressionIsChecked() {
    Diagnostics diagnostics =
        typeCheck(
            "var f = /** @this {string} */ "
                + "function named() { return this - 1; };");

    assertEquals(0, diagnostics.errors);
    assertTrue(
        "A named function expression must not lose its annotated this type",
        diagnostics.warnings > 0);
  }

  @Test
  public void testNumericThisAnnotationAllowsNumericOperation() {
    Diagnostics diagnostics =
        typeCheck("/** @this {number} */ function f() { return this - 1; }");

    assertEquals(0, diagnostics.errors);
    assertEquals(0, diagnostics.warnings);
  }

  @Test
  public void testNumericThisTypeInFunctionSignatureAllowsNumericOperation() {
    Diagnostics diagnostics =
        typeCheck(
            "/** @type {function(this:number)} */ "
                + "var f = function() { return this - 1; };");

    assertEquals(0, diagnostics.errors);
    assertEquals(0, diagnostics.warnings);
  }
}