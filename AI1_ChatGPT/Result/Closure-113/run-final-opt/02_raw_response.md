package com.google.javascript.jscomp;

import org.junit.Test;

public final class ProcessClosurePrimitivesDefectsTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ProcessClosurePrimitives(compiler, null, CheckLevel.ERROR);
  }

  @Test
  public void testMissingNestedRequireReportsMissingProvide() {
    testError(
        "goog.require('namespace.Class1');",
        ProcessClosurePrimitives.MISSING_PROVIDE_ERROR);
  }

  @Test
  public void testProvidedNamespaceSatisfiesRequire() {
    test(
        "goog.provide('provided'); goog.require('provided');",
        "var provided = {};");
  }

  @Test
  public void testRequireBeforeProvideReportsLateProvide() {
    testError(
        "goog.require('namespace.Class1'); goog.provide('namespace.Class1');",
        ProcessClosurePrimitives.LATE_PROVIDE_ERROR);
  }

  @Test
  public void testRequireWithoutStringLiteralIsRejected() {
    testError(
        "goog.require();",
        ProcessClosurePrimitives.INVALID_REQUIRE_ERROR);
  }
}