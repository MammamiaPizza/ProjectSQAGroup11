package com.google.javascript.jscomp;

import java.util.Arrays;
import java.util.Collections;

import com.google.javascript.jscomp.SourceFile;

import junit.framework.TestCase;

public class CompilerDependencySortingWhitespaceTest extends TestCase {

  private CompilerOptions whitespaceOptionsWithDependencySorting() {
    CompilerOptions options = new CompilerOptions();
    CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
    options.setDependencyOptions(
        new DependencyOptions().setDependencySorting(true));
    return options;
  }

  public void testDependencySortingWhitespaceModeOrdersRequiredInputFirst() {
    Compiler compiler = new Compiler();
    SourceFile consumer = SourceFile.fromCode(
        "consumer.js",
        "goog.provide('test.consumer');\n"
            + "goog.require('test.base');\n"
            + "var CONSUMER_MARKER = BASE_MARKER;\n");
    SourceFile base = SourceFile.fromCode(
        "base.js",
        "goog.provide('test.base');\n"
            + "var BASE_MARKER = 1;\n");

    Result result = compiler.compile(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(consumer, base),
        whitespaceOptionsWithDependencySorting());

    assertNotNull(result);
    assertEquals(0, compiler.getErrors().length);

    String[] sources = compiler.toSourceArray();
    assertEquals(2, sources.length);
    assertTrue(sources[0].indexOf("BASE_MARKER") >= 0);
    assertTrue(sources[1].indexOf("CONSUMER_MARKER") >= 0);

    String output = compiler.toSource();
    int basePosition = output.indexOf("var BASE_MARKER");
    int consumerPosition = output.indexOf("var CONSUMER_MARKER");
    assertTrue(basePosition >= 0);
    assertTrue(consumerPosition >= 0);
    assertTrue(basePosition < consumerPosition);
  }

  public void testDependencySortingWhitespaceModeHandlesSingleProvidedInput() {
    Compiler compiler = new Compiler();
    SourceFile onlyInput = SourceFile.fromCode(
        "only.js",
        "goog.provide('test.only');\n"
            + "var SINGLE_INPUT_MARKER = 7;\n");

    Result result = compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(onlyInput),
        whitespaceOptionsWithDependencySorting());

    assertNotNull(result);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(1, compiler.toSourceArray().length);
    assertTrue(compiler.toSource().indexOf("SINGLE_INPUT_MARKER") >= 0);
  }

  public void testDependencySortingWhitespaceModeHandlesMissingRequiredProvide() {
    Compiler compiler = new Compiler();
    SourceFile input = SourceFile.fromCode(
        "missing.js",
        "goog.provide('test.missingConsumer');\n"
            + "goog.require('test.notProvided');\n"
            + "var MISSING_DEPENDENCY_MARKER = 1;\n");

    Result result = compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(input),
        whitespaceOptionsWithDependencySorting());

    assertNotNull(result);
    assertEquals(0, compiler.getErrors().length);
    assertTrue(compiler.toSource().indexOf("MISSING_DEPENDENCY_MARKER") >= 0);
  }
}