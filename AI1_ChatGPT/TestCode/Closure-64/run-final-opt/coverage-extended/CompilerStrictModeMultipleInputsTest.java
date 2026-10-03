package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CompilerStrictModeMultipleInputsTest {

  @Test
  public void reportsLaterStrictModeDiagnosticAtItsActualLineAcrossInputs() {
    StringBuilder secondInput = new StringBuilder();
    secondInput.append("\"use strict\";\n");
    for (int i = 0; i < 15; i++) {
      secondInput.append(";\n");
    }
    secondInput.append("with ({}) {}\n");

    Compiler compiler = compile(
        JSSourceFile.fromCode("first.js", "\"use strict\";\nvar first = 0;\n"),
        JSSourceFile.fromCode("second.js", secondInput.toString()));

    JSError[] diagnostics = compiler.getMessages();
    boolean foundLaterInputDiagnostic = false;
    for (JSError diagnostic : diagnostics) {
      if ("second.js".equals(diagnostic.sourceName)) {
        foundLaterInputDiagnostic = true;
        assertEquals(17, diagnostic.lineNumber);
      }
    }

    assertTrue("Expected a strict-mode diagnostic for the later input",
        foundLaterInputDiagnostic);
  }

  @Test
  public void acceptsRepeatedUseStrictDirectivesInSeparateValidInputs() {
    Compiler compiler = compile(
        JSSourceFile.fromCode("first.js", "\"use strict\";\nvar first = 1;\n"),
        JSSourceFile.fromCode("second.js", "\"use strict\";\nvar second = 2;\n"));

    assertEquals(0, compiler.getErrorCount());
  }

  private Compiler compile(JSSourceFile... inputs) {
    CompilerOptions options = new CompilerOptions();
    options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<JSSourceFile>emptyList(),
        Arrays.asList(inputs),
        options);
    return compiler;
  }
}
