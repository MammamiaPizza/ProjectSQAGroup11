package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class CompilerGlobalThisOffTest {

  @Test
  public void testGlobalThisProducesNoDiagnosticsWhenCheckIsDisabled() {
    CompilerOptions options = new CompilerOptions();
    options.checkGlobalThis = false;

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<JSSourceFile>emptyList(),
        Collections.singletonList(
            JSSourceFile.fromCode("global_this.js", "this.globalValue = 1;")),
        options);

    assertEquals(0, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
    assertEquals(0, compiler.getErrors().length);
    assertEquals(0, compiler.getWarnings().length);
    assertEquals(0, compiler.getMessages().length);
  }

  @Test
  public void testSyntaxErrorsAreStillReportedWhenGlobalThisCheckIsDisabled() {
    CompilerOptions options = new CompilerOptions();
    options.checkGlobalThis = false;

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<JSSourceFile>emptyList(),
        Collections.singletonList(
            JSSourceFile.fromCode("invalid.js", "this.globalValue = ;")),
        options);

    assertTrue(compiler.getErrorCount() > 0);
    assertTrue(compiler.getErrors().length > 0);
  }
}