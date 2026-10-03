package com.google.javascript.jscomp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import junit.framework.TestCase;

public class CommandLineRunnerWarningGuardTest extends TestCase {

  public void testJscompWarningOverridesJscompOffForCheckTypes() throws Exception {
    Result result = compileWithFlags(
        "--jscomp_off=checkTypes",
        "--jscomp_warning=checkTypes");

    assertEquals(0, result.errors.length);
    assertEquals(1, result.warnings.length);
  }

  public void testJscompErrorOverridesJscompOffForCheckTypes() throws Exception {
    Result result = compileWithFlags(
        "--jscomp_off=checkTypes",
        "--jscomp_error=checkTypes");

    assertEquals(1, result.errors.length);
    assertEquals(0, result.warnings.length);
  }

  public void testJscompErrorOverridesJscompWarningForCheckTypes() throws Exception {
    Result result = compileWithFlags(
        "--jscomp_warning=checkTypes",
        "--jscomp_error=checkTypes");

    assertEquals(1, result.errors.length);
    assertEquals(0, result.warnings.length);
  }

  public void testRegisteredDiagnosticGroupCanBeLookedUpByName() {
    DiagnosticGroups groups = new DiagnosticGroups();

    assertTrue(groups.getRegisteredGroups().containsKey("checkTypes"));
    assertSame(groups.getRegisteredGroups().get("checkTypes"),
        groups.forName("checkTypes"));
  }

  public void testUnknownDiagnosticGroupIsRejectedWhenSettingWarningLevel() {
    try {
      new DiagnosticGroups().setWarningLevels(
          new CompilerOptions(),
          Collections.singletonList("not_a_registered_diagnostic_group"),
          CheckLevel.WARNING);
      fail("Unknown diagnostic groups must be rejected");
    } catch (NullPointerException expected) {
      assertTrue(expected.getMessage().contains(
          "No warning class for name: not_a_registered_diagnostic_group"));
    }
  }

  private Result compileWithFlags(String... flags) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ByteArrayOutputStream errors = new ByteArrayOutputStream();
    CommandLineRunner runner = new CommandLineRunner(
        flags, new PrintStream(output), new PrintStream(errors));
    CompilerOptions options = runner.createOptions();
    runner.setRunOptions(options);

    Compiler compiler = new Compiler();
    return compiler.compile(
        JSSourceFile.fromCode("externs.js", ""),
        JSSourceFile.fromCode(
            "input.js",
            "/** @type {number} */ var value = 'not a number';"),
        options);
  }
}