package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class TypeValidatorIssue1047Test {

  @Test
  public void testNestedPropertyAccessReportsDeclaredReceiverType() {
    Compiler compiler = compile(
        "/** @constructor */ function C2() {}\n"
            + "/** @constructor */ function C3() {}\n"
            + "/** @type {C2} */ C3.prototype.c2_;\n"
            + "C3.prototype.c2_.p;");

    assertEquals(1, diagnosticCount(compiler));
    assertEquals("Property p never defined on C2", diagnostic(compiler).description);
  }

  @Test
  public void testNestedPropertyAccessAllowsPropertyDeclaredOnReceiverType() {
    Compiler compiler = compile(
        "/** @constructor */ function C2() {}\n"
            + "/** @type {number} */ C2.prototype.p;\n"
            + "/** @constructor */ function C3() {}\n"
            + "/** @type {C2} */ C3.prototype.c2_;\n"
            + "C3.prototype.c2_.p;");

    assertEquals(0, diagnosticCount(compiler));
  }

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    compiler.compile(
        new SourceFile[] {SourceFile.fromCode("externs.js", "")},
        new SourceFile[] {SourceFile.fromCode("test.js", source)},
        options);
    return compiler;
  }

  private int diagnosticCount(Compiler compiler) {
    return compiler.getErrors().length + compiler.getWarnings().length;
  }

  private JSError diagnostic(Compiler compiler) {
    if (compiler.getErrors().length == 1) {
      return compiler.getErrors()[0];
    }
    return compiler.getWarnings()[0];
  }
}