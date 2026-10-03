package com.google.javascript.jscomp;

import org.junit.Test;

public class NormalizeRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new Normalize(compiler, false);
  }

  @Test
  public void testDuplicateGlobalVarWithoutInitializerIsRemoved() {
    test("var duplicate; var duplicate;", "var duplicate;");
  }

  @Test
  public void testDuplicateGlobalVarWithInitializerBecomesAssignment() {
    test("var duplicate; var duplicate = 1;", "var duplicate; duplicate = 1;");
  }

  @Test
  public void testDuplicateInitializedGlobalVarPreservesFirstInitializer() {
    test("var duplicate = 0; var duplicate = 1;",
        "var duplicate = 0; duplicate = 1;");
  }

  @Test
  public void testDuplicateVarInExternsDoesNotCauseRedeclarationFailure() {
    test("var externName; var externName;", "var externName;", "var externName;");
  }

  @Test
  public void testExternAndSourceDeclarationWithSameNameAreAllowed() {
    test("var sharedName;", "var sharedName;", "var sharedName;");
  }

  @Test
  public void testMakeLocalNamesUniqueAcrossFunctions() {
    test(
        "function f() { var value; } function g() { var value; }",
        "function f() { var value$$f; } function g() { var value$$g; }");
  }

  @Test
  public void testDuplicateLocalDeclarationUsesUniqueLocalName() {
    test(
        "function f() { var value; var value = 1; }",
        "function f() { var value$$f; value$$f = 1; }");
  }
}