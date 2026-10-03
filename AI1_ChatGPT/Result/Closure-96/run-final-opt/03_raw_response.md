package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

public class TypeCheckBug96Test {

  private Result compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);

    return compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test.js", source)),
        options);
  }

  private void assertHasWarning(String source) {
    Result result = compile(source);
    assertEquals("The test source should compile without errors", 0, result.errors.length);
    assertTrue("Expected a type-check warning", result.warnings.length > 0);
  }

  private void assertHasNoWarnings(String source) {
    Result result = compile(source);
    assertEquals("The test source should compile without errors", 0, result.errors.length);
    assertEquals("Did not expect a type-check warning", 0, result.warnings.length);
  }

  @Test
  public void reportsWarningForPrimitiveArgumentMismatch() {
    assertHasWarning(
        "/** @param {number} value */\n"
            + "function takesNumber(value) {}\n"
            + "takesNumber('not a number');");
  }

  @Test
  public void acceptsMatchingPrimitiveArgument() {
    assertHasNoWarnings(
        "/** @param {number} value */\n"
            + "function takesNumber(value) {}\n"
            + "takesNumber(1);");
  }

  @Test
  public void reportsWarningForMissingRequiredArgument() {
    assertHasWarning(
        "/** @param {number} value */\n"
            + "function takesNumber(value) {}\n"
            + "takesNumber();");
  }

  @Test
  public void acceptsOptionalArgumentAtBothArityBoundaries() {
    assertHasNoWarnings(
        "/** @param {number=} value */\n"
            + "function takesOptionalNumber(value) {}\n"
            + "takesOptionalNumber();\n"
            + "takesOptionalNumber(1);");
  }

  @Test
  public void reportsWarningForConstructorFunctionWithWrongConstructedType() {
    assertHasWarning(
        "/** @constructor */\n"
            + "function Expected() {}\n"
            + "/** @constructor */\n"
            + "function Actual() {}\n"
            + "/** @param {function(new:Expected)} ctor */\n"
            + "function takesExpectedConstructor(ctor) {}\n"
            + "takesExpectedConstructor(Actual);");
  }

  @Test
  public void acceptsConstructorFunctionWithMatchingConstructedType() {
    assertHasNoWarnings(
        "/** @constructor */\n"
            + "function Expected() {}\n"
            + "/** @param {function(new:Expected)} ctor */\n"
            + "function takesExpectedConstructor(ctor) {}\n"
            + "takesExpectedConstructor(Expected);");
  }

  @Test
  public void reportsWarningForHigherOrderFunctionReturnMismatch() {
    assertHasWarning(
        "/** @param {function(number):string} fn */\n"
            + "function takesStringProducer(fn) {}\n"
            + "/** @param {number} value @return {number} */\n"
            + "function producesNumber(value) { return value; }\n"
            + "takesStringProducer(producesNumber);");
  }
}