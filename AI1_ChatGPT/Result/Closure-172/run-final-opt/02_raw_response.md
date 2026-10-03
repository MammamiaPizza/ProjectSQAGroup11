package com.google.javascript.jscomp;

import org.junit.Before;
import org.junit.Test;

public final class TypedScopeCreatorIssue1024Test extends CompilerTestCase {

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    enableTypeCheck();
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