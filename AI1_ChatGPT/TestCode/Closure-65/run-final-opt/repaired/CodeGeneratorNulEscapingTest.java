package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CodeGeneratorNulEscapingTest {

  @Test
  public void testNulIsEscapedAsThreeDigitOctalInDoubleQuotedString() {
    assertEquals("\"\\000\"", CodeGenerator.escapeToDoubleQuotedJsString("\0"));
  }

  @Test
  public void testNulBeforeDigitUsesThreeDigitOctalEscape() {
    assertEquals("\"\\0001\"", CodeGenerator.escapeToDoubleQuotedJsString("\0" + "1"));
  }

  @Test
  public void testNulBetweenPrintableCharactersUsesThreeDigitOctalEscape() {
    assertEquals("\"a\\000b\"", CodeGenerator.escapeToDoubleQuotedJsString("a\0b"));
  }

  @Test
  public void testPrintableStringIsDoubleQuotedWithoutUnnecessaryEscaping() {
    assertEquals("\"plain text 123\"", CodeGenerator.escapeToDoubleQuotedJsString("plain text 123"));
  }

  @Test
  public void testDoubleQuoteAndBackslashAreEscaped() {
    assertEquals(
        "\"a\\\"b\\\\c\"",
        CodeGenerator.escapeToDoubleQuotedJsString("a\"b\\c"));
  }
}
