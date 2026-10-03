package com.google.javascript.jscomp;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypedScopeCreatorLendsTest {

  @Test
  public void testLendsPropertyConflictingWithPreviouslyInferredReturnReportsError() {
    String diagnostics = compile(
        "/** @constructor */ function Foo() {}\n"
            + "/** @lends {Foo.prototype} */\n"
            + "var members = { method: function() { return 1; } };\n"
            + "Foo.prototype.method = function() { return 'wrong'; };\n");

    assertTrue(
        "Expected an inconsistent return type diagnostic, but got:\n" + diagnostics,
        diagnostics.contains("inconsistent return type"));
  }

  @Test
  public void testLendsPropertyConflictingWithExistingReturnReportsError() {
    String diagnostics = compile(
        "/** @constructor */ function Foo() {}\n"
            + "Foo.prototype.method = function() { return 1; };\n"
            + "/** @lends {Foo.prototype} */\n"
            + "var members = { method: function() { return 'wrong'; } };\n");

    assertTrue(
        "Expected an inconsistent return type diagnostic, but got:\n" + diagnostics,
        diagnostics.contains("inconsistent return type"));
  }

  @Test
  public void testNestedObjectLiteralIsNotAttributedToLendsTarget() {
    String diagnostics = compile(
        "/** @constructor */ function Foo() {}\n"
            + "Foo.prototype.method = function() { return 1; };\n"
            + "/** @lends {Foo.prototype} */\n"
            + "var members = {\n"
            + "  method: function() {\n"
            + "    var nested = { method: function() { return 'nested'; } };\n"
            + "    return 1;\n"
            + "  }\n"
            + "};\n");

    assertFalse(
        "A nested object literal must not change the lends target's return type:\n"
            + diagnostics,
        diagnostics.contains("inconsistent return type"));
  }

  private String compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.ERROR);

    compiler.compile(
        JSSourceFile.fromCode("externs.js", ""),
        JSSourceFile.fromCode("input.js", source),
        options);

    StringBuilder diagnostics = new StringBuilder();
    for (JSError error : compiler.getErrors()) {
      diagnostics.append(error.description).append('\n');
    }
    for (JSError warning : compiler.getWarnings()) {
      diagnostics.append(warning.description).append('\n');
    }
    return diagnostics.toString();
  }
}