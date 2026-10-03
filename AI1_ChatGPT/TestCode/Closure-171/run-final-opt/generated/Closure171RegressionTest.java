package com.google.javascript.jscomp;

import org.junit.Test;

public class Closure171RegressionTest extends CompilerTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    enableTypeCheck();
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
