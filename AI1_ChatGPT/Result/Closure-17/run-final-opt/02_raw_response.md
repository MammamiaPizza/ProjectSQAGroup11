package com.google.javascript.jscomp;

import com.google.javascript.rhino.JSError;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypedScopeCreatorIssue688Test {

  @Test
  public void testPrototypeFunctionReturnTypeMismatchIsReported() {
    String source =
        "/** @constructor */ function Foo() {}\n"
            + "/** @return {number} */\n"
            + "Foo.prototype.value = function() {\n"
            + "  return 'not a number';\n"
            + "};\n";

    com.google.javascript.jscomp.Compiler compiler = compile(source);

    assertTrue(
        "A JSDoc-typed prototype function must report its inconsistent return type.",
        hasInconsistentReturnTypeDiagnostic(compiler));
  }

  @Test
  public void testConstructorAssignedFunctionReturnTypeMismatchIsReported() {
    String source =
        "/** @constructor */\n"
            + "function Foo() {\n"
            + "  /** @return {number} */\n"
            + "  this.value = function() {\n"
            + "    return 'not a number';\n"
            + "  };\n"
            + "}\n";

    com.google.javascript.jscomp.Compiler compiler = compile(source);

    assertTrue(
        "A function declared on a constructor instance must retain its return annotation.",
        hasInconsistentReturnTypeDiagnostic(compiler));
  }

  @Test
  public void testVariableAssignedFunctionReturnTypeMismatchIsReported() {
    String source =
        "/** @return {number} */\n"
            + "var value = function() {\n"
            + "  return 'not a number';\n"
            + "};\n";

    com.google.javascript.jscomp.Compiler compiler = compile(source);

    assertTrue(
        "A function expression assigned to a typed variable must be checked against its return type.",
        hasInconsistentReturnTypeDiagnostic(compiler));
  }

  @Test
  public void testMatchingAnnotatedReturnTypeDoesNotReportInconsistency() {
    String source =
        "/** @constructor */ function Foo() {}\n"
            + "/** @return {number} */\n"
            + "Foo.prototype.value = function() {\n"
            + "  return 1;\n"
            + "};\n";

    com.google.javascript.jscomp.Compiler compiler = compile(source);

    assertFalse(
        "A return value matching the declared type must not be reported as inconsistent.",
        hasInconsistentReturnTypeDiagnostic(compiler));
  }

  private com.google.javascript.jscomp.Compiler compile(String source) {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(SourceFile.fromCode("issue688.js", source)),
        options);
    return compiler;
  }

  private boolean hasInconsistentReturnTypeDiagnostic(
      com.google.javascript.jscomp.Compiler compiler) {
    return containsInconsistentReturnType(compiler.getWarnings())
        || containsInconsistentReturnType(compiler.getErrors());
  }

  private boolean containsInconsistentReturnType(JSError[] diagnostics) {
    for (JSError diagnostic : diagnostics) {
      if (diagnostic.description != null
          && diagnostic.description.contains("inconsistent return type")) {
        return true;
      }
    }
    return false;
  }
}