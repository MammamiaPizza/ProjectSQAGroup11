package com.google.javascript.jscomp;

import org.junit.Test;

public final class FunctionTypeBuilderRegressionTest extends CompilerTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    enableTypeCheck();
  }

  @Test
  public void testOverriddenMethodRetainsInheritedTrailingParameterType() {
    testTypes(
        "/** @constructor */\n"
            + "function Foo() {}\n"
            + "/**\n"
            + " * @param {number} first\n"
            + " * @param {number} second\n"
            + " */\n"
            + "Foo.prototype.add = function(first, second) {};\n"
            + "/**\n"
            + " * @constructor\n"
            + " * @extends {Foo}\n"
            + " */\n"
            + "function Bar() {}\n"
            + "/** @override */\n"
            + "Bar.prototype.add = function(first) {};\n"
            + "new Bar().add(1, 'not a number');",
        "actual parameter 2 of Bar.prototype.add does not match formal parameter\n"
            + "found   : string\n"
            + "required: number");
  }

  @Test
  public void testOverriddenMethodAcceptsCompatibleInheritedTrailingParameter() {
    testTypes(
        "/** @constructor */\n"
            + "function Foo() {}\n"
            + "/**\n"
            + " * @param {number} first\n"
            + " * @param {number} second\n"
            + " */\n"
            + "Foo.prototype.add = function(first, second) {};\n"
            + "/**\n"
            + " * @constructor\n"
            + " * @extends {Foo}\n"
            + " */\n"
            + "function Bar() {}\n"
            + "/** @override */\n"
            + "Bar.prototype.add = function(first) {};\n"
            + "new Bar().add(1, 2);",
        null);
  }
}