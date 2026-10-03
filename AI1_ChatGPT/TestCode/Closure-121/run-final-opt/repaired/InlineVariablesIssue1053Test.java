package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class InlineVariablesIssue1053Test extends CompilerTestCase {

  public InlineVariablesIssue1053Test() {
    super();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineVariables(
        compiler, InlineVariables.Mode.ALL, true);
  }

  @Test
  public void testExternalIssue1053AliasOfCallResultKeepsReferencedVariable() {
    test(
        "function f(){var y=g();var x=y;return x;}",
        "function f(){var y=g();return y;}");
  }

  @Test
  public void testInlinesImmutableLocalDeclaration() {
    test(
        "function f(){var x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesSeparatedDeclarationAndInitialization() {
    test(
        "function f(){var x;x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testDoesNotInlineVariableWithLaterAssignment() {
    testSame(
        "function f(){var x=1;x=2;return x;}");
  }

  @Test
  public void testDoesNotInlineVariableUsedAsLValue() {
    testSame(
        "function f(){var x=1;x++;return x;}");
  }

  @Test
  public void testDoesNotInlineUninitializedVariable() {
    testSame(
        "function f(){var x;return x;}");
  }
}

abstract class CompilerTestCase {

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    Node actual = compile(input, true);
    Node expectedRoot = compile(expected, false);
    assertEquals(expectedRoot.toStringTree(), actual.toStringTree());
  }

  protected void testSame(String input) {
    test(input, input);
  }

  private Node compile(String source, boolean runProcessor) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    compiler.parseInputs();
    if (runProcessor) {
      getProcessor(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());
    }
    return compiler.getJsRoot();
  }
}
