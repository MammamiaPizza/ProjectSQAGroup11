package com.google.javascript.jscomp;

import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeInferenceIssue1056Test {

  @Test
  public void testInferredCallbackParameterTypeReportsInvalidArithmetic() {
    Result result = compile(
        "/** @param {function(string): number} callback */\n"
            + "function invoke(callback) {\n"
            + "  return callback('text');\n"
            + "}\n"
            + "invoke(function(value) {\n"
            + "  return value - 1;\n"
            + "});\n");

    assertEquals("The test program should parse and compile without errors.",
        0, result.errors.length);
    assertTrue(
        "A callback parameter inferred as string must produce a type warning "
            + "when used as a number.",
        result.warnings.length > 0);
  }

  @Test
  public void testInferredCallbackParameterTypeAllowsCompatibleArithmetic() {
    Result result = compile(
        "/** @param {function(number): number} callback */\n"
            + "function invoke(callback) {\n"
            + "  return callback(1);\n"
            + "}\n"
            + "invoke(function(value) {\n"
            + "  return value - 1;\n"
            + "});\n");

    assertEquals("The test program should parse and compile without errors.",
        0, result.errors.length);
    assertEquals(
        "A callback parameter inferred as number should be usable in arithmetic.",
        0, result.warnings.length);
  }

  @Test
  public void testMissingRequiredCallbackArgumentProducesWarning() {
    Result result = compile(
        "/** @param {function(string): number} callback */\n"
            + "function invoke(callback) {\n"
            + "  return callback('text');\n"
            + "}\n"
            + "invoke();\n");

    assertEquals("The test program should parse and compile without errors.",
        0, result.errors.length);
    assertTrue(
        "Calling a function without its required callback argument must warn.",
        result.warnings.length > 0);
  }

  private Result compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);

    return compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("issue1056.js", source)),
        options);
  }
}
