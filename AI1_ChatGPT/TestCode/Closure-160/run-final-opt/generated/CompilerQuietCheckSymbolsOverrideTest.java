package com.google.javascript.jscomp;

import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CompilerQuietCheckSymbolsOverrideTest {

  private Compiler compileWithOptions(CompilerOptions options) {
    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<JSSourceFile>emptyList(),
        Collections.singletonList(
            JSSourceFile.fromCode("input.js", "missingSymbol;")),
        options);
    return compiler;
  }

  @Test
  public void testQuietModeSuppressesUndefinedSymbolWarningWithoutOverride() {
    CompilerOptions options = new CompilerOptions();
    options.checkSymbols = false;

    Compiler compiler = compileWithOptions(options);

    assertEquals(0, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
  }

  @Test
  public void testCheckSymbolsWarningOverrideIsHonoredWhenQuiet() {
    CompilerOptions options = new CompilerOptions();
    options.checkSymbols = false;
    options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);

    Compiler compiler = compileWithOptions(options);

    assertEquals(0, compiler.getErrorCount());
    assertEquals(1, compiler.getWarningCount());
    assertEquals(1, compiler.getWarnings().length);
  }

  @Test
  public void testCheckSymbolsErrorOverrideIsHonoredWhenQuiet() {
    CompilerOptions options = new CompilerOptions();
    options.checkSymbols = false;
    options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.ERROR);

    Compiler compiler = compileWithOptions(options);

    assertEquals(1, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
    assertEquals(1, compiler.getErrors().length);
  }
}
