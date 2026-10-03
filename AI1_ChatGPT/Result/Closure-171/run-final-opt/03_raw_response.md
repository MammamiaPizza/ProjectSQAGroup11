package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class Closure171RegressionTest extends TestCase {

  private Result compile(String source) {
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);

    Compiler compiler = new Compiler();
    return compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input.js", source)),
        options);
  }

  private void testWarning(String source, DiagnosticType expectedWarning) {
    Result result = compile(source);
    assertEquals(0, result.errors.length);
    assertEquals(1, result.warnings.length);
    assertEquals(expectedWarning, result.warnings[0].type);
  }

  private void testSame(String source) {
    Result result = compile(source);
    assertEquals(0, result.errors.length);
    assertEquals(0, result.warnings.length);
  }

  @Test
  public void testPropertyCallChecksAnnotatedMethodArgument() {
    testWarning(
        "/** @constructor */ function Foo() {}\n"
            + "/** @param {number} n */\n"
            + "Foo.prototype.method = function(n) {};\n"
            + "var foo = new Foo();\n"
            + "foo.method('not a number');",
        TypeCheck.INVALID_ARGUMENT_TYPE);
  }

  @Test
  public void testMethodAssignedBeforeFunctionDeclarationRetainsCallableType() {
    testWarning(
        "var namespace = {};\n"
            + "namespace.method = method;\n"
            + "function method(value) {}\n"
            + "namespace.method();",
        TypeCheck.WRONG_ARGUMENT_COUNT);
  }

  @Test
  public void testDeclaredInterfacePropertyCanBeImplemented() {
    testSame(
        "/** @interface */\n"
            + "function InterfaceType() {}\n"
            + "/** @type {number} */\n"
            + "InterfaceType.prototype.value;\n"
            + "/** @constructor\n"
            + " * @implements {InterfaceType}\n"
            + " */\n"
            + "function Implementation() {}\n"
            + "Implementation.prototype.value = 0;");
  }
}