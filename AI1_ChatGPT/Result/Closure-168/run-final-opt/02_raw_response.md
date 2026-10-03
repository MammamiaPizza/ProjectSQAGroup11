package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.JSError;
import org.junit.Test;

public class TypedScopeCreatorEnumInitializerTest {

  private static final String ENUM_INITIALIZER_MESSAGE =
      "enum initializer must be an object literal or an enum";

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    compiler.compile(
        SourceFile.fromCode("externs.js", ""),
        SourceFile.fromCode("test.js", source),
        options);
    return compiler;
  }

  private boolean hasEnumInitializerWarning(Compiler compiler) {
    for (JSError warning : compiler.getWarnings()) {
      if (warning.description.contains(ENUM_INITIALIZER_MESSAGE)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void enumInitializedWithObjectLiteralDoesNotReportInitializerWarning() {
    Compiler compiler =
        compile(
            "/** @enum {number} */\n"
                + "var Numbers = { ONE: 1, TWO: 2 };\n");

    assertEquals(0, compiler.getErrors().length);
    assertFalse(hasEnumInitializerWarning(compiler));
  }

  @Test
  public void enumInitializedFromAnotherEnumDoesNotReportInitializerWarning() {
    Compiler compiler =
        compile(
            "/** @enum {number} */\n"
                + "var Original = { ONE: 1 };\n"
                + "/** @enum {number} */\n"
                + "var Copy = Original;\n");

    assertEquals(0, compiler.getErrors().length);
    assertFalse(hasEnumInitializerWarning(compiler));
  }

  @Test
  public void enumInitializedFromNonEnumNameReportsInitializerWarning() {
    Compiler compiler =
        compile(
            "/** @type {number} */\n"
                + "var ordinaryValue = 1;\n"
                + "/** @enum {number} */\n"
                + "var InvalidEnum = ordinaryValue;\n");

    assertEquals(0, compiler.getErrors().length);
    assertTrue(hasEnumInitializerWarning(compiler));
  }
}