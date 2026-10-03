package com.google.javascript.jscomp;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TypeCheckInterfaceInheritanceRegressionTest {

  @Test
  public void testNonExistentExtendedInterfacesDoNotCrashTypeChecking() {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);

    Throwable failure = null;
    try {
      compiler.compile(
          SourceFile.fromCode("externs.js", ""),
          SourceFile.fromCode(
              "test.js",
              "/** @interface\n"
                  + " * @extends {NonExistentInterfaceOne}\n"
                  + " * @extends {NonExistentInterfaceTwo}\n"
                  + " */\n"
                  + "function TestInterface() {}\n"),
          options);
    } catch (Throwable t) {
      failure = t;
    }

    assertNull(
        "Type checking an interface that extends nonexistent interfaces must complete "
            + "and preserve the harness's expected diagnostic behavior.",
        failure);
  }
}