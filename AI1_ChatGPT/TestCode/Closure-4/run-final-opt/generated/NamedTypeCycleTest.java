package com.google.javascript.jscomp;

import com.google.javascript.rhino.ErrorReporter;
import com.google.javascript.rhino.jstype.JSType;
import java.util.Collections;
import junit.framework.TestCase;

public class NamedTypeCycleTest extends TestCase {

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    return compiler;
  }

  private int countDiagnosticsContaining(Compiler compiler, String text) {
    int count = 0;
    for (JSError error : compiler.getErrors()) {
      if (error.description.contains(text)) {
        count++;
      }
    }
    for (JSError warning : compiler.getWarnings()) {
      if (warning.description.contains(text)) {
        count++;
      }
    }
    return count;
  }

  public void testConstructorImplementingItselfReportsInheritanceCycle() {
    Compiler compiler = compile(
        "/** @constructor @implements {T} */\n"
        + "function T() {}\n");

    assertEquals(
        1,
        countDiagnosticsContaining(
            compiler, "Cycle detected in inheritance chain of type T"));
    assertEquals(
        0, countDiagnosticsContaining(compiler, "can only implement interfaces"));
  }

  public void testExtendsImplementsLoopDoesNotProduceDuplicateNonInterfaceWarnings() {
    Compiler compiler = compile(
        "/** @interface @extends {T} */\n"
        + "function S() {}\n"
        + "/** @constructor @implements {S} */\n"
        + "function T() {}\n");

    assertEquals(
        1, countDiagnosticsContaining(compiler, "can only implement interfaces"));
  }

  public void testInterfaceConversionThroughRecursiveConstructorTerminates() {
    Compiler compiler = compile(
        "/** @interface @extends {T} */\n"
        + "function S() {}\n"
        + "/** @constructor @implements {S} */\n"
        + "function T() {}\n"
        + "/** @type {S} */\n"
        + "var value = new T();\n");

    assertNotNull(compiler);
    assertTrue(
        countDiagnosticsContaining(
            compiler, "Cycle detected in inheritance chain of type") > 0);
  }

  public void testOrdinaryInterfaceToImplementingConstructorConversionIsAccepted() {
    Compiler compiler = compile(
        "/** @interface */\n"
        + "function I() {}\n"
        + "/** @constructor @implements {I} */\n"
        + "function C() {}\n"
        + "/** @type {I} */\n"
        + "var value = new C();\n");

    assertEquals(
        0, countDiagnosticsContaining(compiler, "can only implement interfaces"));
    assertEquals(
        0, countDiagnosticsContaining(compiler, "Cycle detected in inheritance chain"));
  }
}
