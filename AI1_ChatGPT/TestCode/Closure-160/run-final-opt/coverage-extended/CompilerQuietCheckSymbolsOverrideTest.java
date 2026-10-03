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

@org.junit.Test
public void testAcceptEcmaScript5MatchesConfiguredLanguageMode() {
  com.google.javascript.jscomp.Compiler ecmaScript3Compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions ecmaScript3Options =
      new com.google.javascript.jscomp.CompilerOptions();
  ecmaScript3Options.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT3);
  ecmaScript3Compiler.initOptions(ecmaScript3Options);
  org.junit.Assert.assertFalse(ecmaScript3Compiler.acceptEcmaScript5());

  com.google.javascript.jscomp.Compiler ecmaScript5Compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions ecmaScript5Options =
      new com.google.javascript.jscomp.CompilerOptions();
  ecmaScript5Options.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5);
  ecmaScript5Compiler.initOptions(ecmaScript5Options);
  org.junit.Assert.assertTrue(ecmaScript5Compiler.acceptEcmaScript5());

  com.google.javascript.jscomp.Compiler strictCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions strictOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  strictOptions.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
  strictCompiler.initOptions(strictOptions);
  org.junit.Assert.assertTrue(strictCompiler.acceptEcmaScript5());
}

@org.junit.Test
public void testAcceptConstKeywordMatchesConfiguredOption() {
  com.google.javascript.jscomp.Compiler rejectingCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions rejectingOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  rejectingOptions.setAcceptConstKeyword(false);
  rejectingCompiler.initOptions(rejectingOptions);
  org.junit.Assert.assertFalse(rejectingCompiler.acceptConstKeyword());

  com.google.javascript.jscomp.Compiler acceptingCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions acceptingOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  acceptingOptions.setAcceptConstKeyword(true);
  acceptingCompiler.initOptions(acceptingOptions);
  org.junit.Assert.assertTrue(acceptingCompiler.acceptConstKeyword());
}
}
