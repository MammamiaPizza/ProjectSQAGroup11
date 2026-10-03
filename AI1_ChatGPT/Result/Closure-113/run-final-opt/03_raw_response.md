package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.Test;

public final class ProcessClosurePrimitivesDefectsTest {

  private Compiler runProcessor(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    compiler.parseInputs();

    Node root = compiler.getRoot();
    new ProcessClosurePrimitives(compiler, null, CheckLevel.ERROR)
        .process(root.getFirstChild(), root.getLastChild());
    return compiler;
  }

  private void assertError(String source, DiagnosticType error) {
    Compiler compiler = runProcessor(source);
    assertEquals(1, compiler.getErrorCount());
    assertEquals(error, compiler.getErrors()[0].type);
  }

  @Test
  public void testMissingNestedRequireReportsMissingProvide() {
    assertError(
        "goog.require('namespace.Class1');",
        ProcessClosurePrimitives.MISSING_PROVIDE_ERROR);
  }

  @Test
  public void testProvidedNamespaceSatisfiesRequire() {
    Compiler compiler = runProcessor(
        "goog.provide('provided'); goog.require('provided');");
    assertEquals(0, compiler.getErrorCount());
    assertEquals("var provided={};", compiler.toSource(compiler.getRoot().getLastChild()));
  }

  @Test
  public void testRequireBeforeProvideReportsLateProvide() {
    assertError(
        "goog.require('namespace.Class1'); goog.provide('namespace.Class1');",
        ProcessClosurePrimitives.LATE_PROVIDE_ERROR);
  }

  @Test
  public void testRequireWithoutStringLiteralIsRejected() {
    assertError(
        "goog.require();",
        ProcessClosurePrimitives.INVALID_REQUIRE_ERROR);
  }
}