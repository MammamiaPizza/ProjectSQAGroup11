package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
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

abstract class CompilerTestCase extends junit.framework.TestCase {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String source, String expected) {
    test("", source, expected);
  }

  protected void test(String externs, String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.singletonList(SourceFile.fromCode("externs.js", externs)),
        Collections.singletonList(SourceFile.fromCode("input.js", source)),
        new CompilerOptions());

    Node root = compiler.parseInputs();
    Node externsRoot = root.getFirstChild();
    Node jsRoot = externsRoot.getNext();
    getProcessor(compiler).process(externsRoot, jsRoot);

    assertEquals(0, compiler.getErrors().length);

    Compiler expectedCompiler = new Compiler();
    expectedCompiler.init(
        Collections.singletonList(SourceFile.fromCode("externs.js", "")),
        Collections.singletonList(SourceFile.fromCode("expected.js", expected)),
        new CompilerOptions());
    Node expectedRoot = expectedCompiler.parseInputs();
    Node expectedJsRoot = expectedRoot.getFirstChild().getNext();

    assertEquals(expectedCompiler.toSource(expectedJsRoot), compiler.toSource(jsRoot));
  }
}