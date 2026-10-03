package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public final class TypedScopeCreatorIssue1024Test {

  @Before
  public void setUp() throws Exception {
  }

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    Result result =
        compiler.compile(
            Arrays.asList(SourceFile.fromCode("externs", "")),
            Arrays.asList(SourceFile.fromCode("test", source)),
            options);
    assertEquals(0, result.errors.length);
    assertEquals(0, result.warnings.length);
  }

  @Test
  public void testForwardReferenceToObjectLiteralEnumInAnnotation() {
    testSame(
        "/** @param {Later} value */\n"
            + "function consume(value) {}\n"
            + "/** @enum {number} */\n"
            + "var Later = { VALUE: 1 };\n"
            + "consume(Later.VALUE);");
  }

  @Test
  public void testEnumObjectLiteralMembersHaveEnumElementType() {
    testSame(
        "/** @enum {string} */\n"
            + "var Status = { OK: 'ok', ERROR: 'error' };\n"
            + "/** @param {Status} status */\n"
            + "function setStatus(status) {}\n"
            + "setStatus(Status.OK);\n"
            + "setStatus(Status.ERROR);");
  }

  @Test
  public void testEnumMayBeInitializedFromAnotherEnum() {
    testSame(
        "/** @enum {number} */\n"
            + "var Original = { ONE: 1 };\n"
            + "/** @param {Alias} value */\n"
            + "function consume(value) {}\n"
            + "/** @enum {number} */\n"
            + "var Alias = Original;\n"
            + "consume(Alias.ONE);");
  }
}
