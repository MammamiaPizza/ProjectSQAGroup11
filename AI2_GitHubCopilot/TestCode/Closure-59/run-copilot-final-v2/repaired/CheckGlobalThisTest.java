package com.google.javascript.jscomp;

import com.google.common.collect.Lists;
import junit.framework.TestCase;

import java.util.List;

/**

 - Tests for global-this check suppression.
 - See bug 521: when --check_global_this_level=off, no warnings or errors
 - should be reported for top-level "this".
  */
 public class CheckGlobalThisTest extends TestCase {
  private static final String TOP_LEVEL_THIS_JS = "this.foo = 1;";
  private static final String NO_THIS_JS = "var x = 1;";
  private static final String THIS_INSIDE_FUNCTION_JS =
      "function f() { this.bar = 2; }";
  private static final String MULTIPLE_TOP_LEVEL_THIS_JS =
      "this.a = 1; this.b = 2;";
  private CompilerOptions newOptions(CheckLevel level) {
  CompilerOptions options = new CompilerOptions();
  options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, level);
  return options;
  }
  private void compileAndAssert(String js, CheckLevel level,
                            int expectedErrors, int expectedWarnings) {
  Compiler compiler = new Compiler();
  List<JSSourceFile> externs = Lists.newArrayList();
  List<JSSourceFile> inputs = Lists.newArrayList(
          JSSourceFile.fromCode("test.js", js));
  compiler.compile(externs, inputs, newOptions(level));
  assertEquals("Unexpected number of errors",
          expectedErrors, compiler.getErrorCount());
  assertEquals("Unexpected number of warnings",
          expectedWarnings, compiler.getWarningCount());
  }
  // ---- Check OFF should never produce warnings/errors ----
  public void testGlobalThisOff_NoWarningsOrErrors() {
  compileAndAssert(TOP_LEVEL_THIS_JS, CheckLevel.OFF, 0, 0);
  }
  public void testNoGlobalThisOff_Baseline() {
  compileAndAssert(NO_THIS_JS, CheckLevel.OFF, 0, 0);
  }
  public void testThisInsideFunctionOff_NoWarnings() {
  compileAndAssert(THIS_INSIDE_FUNCTION_JS, CheckLevel.OFF, 0, 0);
  }
  public void testMultipleGlobalThisOff_NoWarnings() {
  compileAndAssert(MULTIPLE_TOP_LEVEL_THIS_JS, CheckLevel.OFF, 0, 0);
  }
  // ---- Check WARNING should produce only warnings, no errors ----
  public void testGlobalThisOn_WarningProduced() {
  compileAndAssert(TOP_LEVEL_THIS_JS, CheckLevel.WARNING, 0, 1);
  }
  public void testMultipleGlobalThisOn_MultipleWarnings() {
  compileAndAssert(MULTIPLE_TOP_LEVEL_THIS_JS, CheckLevel.WARNING, 0, 2);
  }
  // ---- Check EROR should produce errors ----
  public void testGlobalThisError_ErrorsProduced() {
  compileAndAssert(TOP_LEVEL_THIS_JS, CheckLevel.EROR, 1, 0);
  }
  // ---- Inside function triggers a warning when check is on ----
  public void testThisInsideFunctionOn_Warning() {
  compileAndAssert(THIS_INSIDE_FUCTION_JS, CheckLevel.WARNING, 0, 1);
  }
  // ---- Sanity check: ordinary code without this is clean even with check on ----
  public void testNoThisOn_NoWarnings() {
  compileAndAssert(NO_THIS_JS, CheckLevel.EROR, 0, 0);
  }

}
