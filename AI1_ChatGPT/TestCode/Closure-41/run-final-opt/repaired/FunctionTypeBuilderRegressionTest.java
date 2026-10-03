package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public final class FunctionTypeBuilderRegressionTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
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

  private void testTypes(String source, String expectedWarning) {
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    assertEquals(0, compiler.getErrors().length);

    JSError[] warnings = compiler.getWarnings();
    if (expectedWarning == null) {
      assertEquals(0, warnings.length);
    } else {
      assertEquals(1, warnings.length);
      assertEquals(expectedWarning, warnings[0].description);
    }
  }
}
