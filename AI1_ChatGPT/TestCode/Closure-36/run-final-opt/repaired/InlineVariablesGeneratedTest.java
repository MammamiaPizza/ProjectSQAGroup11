package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class InlineVariablesGeneratedTest {

  private String transform(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(SourceFile.fromCode("input", source)),
        options);
    compiler.parseInputs();
    new InlineVariables(compiler, InlineVariables.Mode.ALL, true)
        .process(compiler.getExternsRoot(), compiler.getJsRoot());
    return compiler.toSource();
  }

  private String normalize(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(SourceFile.fromCode("input", source)),
        options);
    compiler.parseInputs();
    return compiler.toSource();
  }

  private void test(String source, String expected) {
    assertEquals(normalize(expected), transform(source));
  }

  private void testSame(String source) {
    test(source, source);
  }

  @Test
  public void testInlinesImmutableLocalIntoSingleRead() {
    test(
        "function f(){var x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesSeparateInitialization() {
    test(
        "function f(){var x;x=1;return x;}",
        "function f(){return 1;}");
  }

  @Test
  public void testInlinesImmutableLocalIntoMultipleReads() {
    test(
        "function f(){var x=1;return x+x;}",
        "function f(){return 1+1;}");
  }

  @Test
  public void testDoesNotInlineVariableWithLaterAssignment() {
    testSame("function f(){var x=1;x=2;return x;}");
  }

  @Test
  public void testDoesNotInlineReadBeforeInitialization() {
    testSame("function f(){var x;x;x=1;return x;}");
  }

  @Test
  public void testDoesNotInlineRenamePropertyFunctionVariable() {
    testSame(
        "function f(){"
            + "var JSCompiler_renameProperty='p';"
            + "return JSCompiler_renameProperty;"
            + "}");
  }

  @Test
  public void testDoesNotInlineSingletonGetterClassFunction() {
    testSame(
        "function f(){"
            + "var x=function(){};"
            + "goog.addSingletonGetter(x);"
            + "}");
  }
}
