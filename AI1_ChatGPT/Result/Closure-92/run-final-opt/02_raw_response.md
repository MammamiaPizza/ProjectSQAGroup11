package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Test;

public class ProcessClosurePrimitivesAdditionalTest extends CompilerTestCase {

  private final CheckLevel requiresLevel = CheckLevel.ERROR;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ProcessClosurePrimitives(compiler, requiresLevel, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testTopLevelProvideCreatesNamespaceVariable() {
    test("goog.provide('foo');", "var foo = {};");
  }

  @Test
  public void testNestedProvideCreatesAllNamespaceLevels() {
    test(
        "goog.provide('foo.bar');",
        "var foo = {};foo.bar = {};");
  }

  @Test
  public void testRequireOfPreviouslyProvidedNamespaceIsRemoved() {
    test(
        "goog.provide('foo');goog.require('foo');",
        "var foo = {};");
  }

  @Test
  public void testMissingRequireReportsMissingProvideError() {
    testError(
        "goog.require('missing.namespace');",
        ProcessClosurePrimitives.MISSING_PROVIDE_ERROR);
  }

  @Test
  public void testRequireBeforeProvideReportsLateProvideError() {
    testError(
        "goog.require('foo');goog.provide('foo');",
        ProcessClosurePrimitives.LATE_PROVIDE_ERROR);
  }

  @Test
  public void testIndependentModulesKeepSeparateTopLevelProvidesInTheirModules() {
    JSModule first = new JSModule("first");
    first.add(SourceFile.fromCode("first.js", "goog.provide('first');"));

    JSModule second = new JSModule("second");
    second.add(SourceFile.fromCode("second.js", "goog.provide('second');"));

    test(
        new JSModule[] {first, second},
        new String[] {"var first = {};", "var second = {};"});
  }
}