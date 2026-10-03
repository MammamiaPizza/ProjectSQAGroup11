package com.google.javascript.jscomp;

import junit.framework.TestCase;
import java.nio.charset.CharsetEncoder;

/**
 *

 - Tests for CodeGenerator string escaping, targeting the null character bug
 - where U+0000 is incorrectly escaped as \u0000 instead of \0.
  /
 public class CodeGeneratorTest extends TestCase { /*
  - Core bug: null character must be escaped as \0, not \u0000.
  - This reproduces the failing CodePrinterTest::testZero expectation.
  */
  public void testNullCharEscapedAsBackslashZero() {
   String result = CodeGenerator.escapeToDoubleQuotedJsString("\0");
   assertTrue(result.contains("\0"));
   assertFalse(result.contains("\u0000"));
  }

  /**

 - Null char followed by literal '0': must produce \00, not broken \u00000.
    */
   public void testNullCharFollowedByZeroDigit() {
 String result = CodeGenerator.escapeToDoubleQuotedJsString("\00");
 assertTrue(result.contains("\00"));
 assertFalse(result.contains("\u0000"));
   }

  public void testNullCharAtStrt() {
    String result = CodeGenerator.escapeToDoubleQuotedJsString("\0hello");
    assertTrue(result.contains("\0hello"));
    assertFalse(result.contains("\u0000")); }

  public void testNullCharAtEnd() {
    String result = CodeGenerator.escapeToDoubleQuotedJsString("world\0");
    assertTrue(result.contains("world\0"));
    assertFalse(result.contains("\u0000")); }

  public void testMultipleullChars() {
    String result = CodeGenerator.escapeToDoubleQuotedJsString("\0\0\0");
    assertTrue(result.contains("\0\0\0"));
    assertFalse(result.contains("\u0000")); }

  public void testOnlyNullChar() {
    String result = CodeGenerator.escapeToDoubleQuotedJsString("\0");
    assertTrue(result.startsWith("""));
    assertTrue(result.endsWith("""));
    // should contain exactly one escape
    int idx = result.indexOf("\0");
    assertTrue(idx >= 0);
    // no second occurrence not counting the same chars twice
    assertEquals(-1, result.indexOf("\0", idx + 2)); }

  public void testControlCharacterShortEscapes() {
    // Known single-character escape sequences per ECMA-262
    assertEquals(""\b"", CodeGenerator.escapeToDoubleQuotedJsString("\b"));
    assertEquals(""\t"", CodeGenerator.escapeToDoubleQuotedJsString("\t"));
    assertEquals(""\n"", CodeGenerator.escapeToDoubleQuotedJsString("\n"));
    assertEquals(""\f"", CodeGenerator.escapeToDoubleQuotedJsString("\f"));
    assertEquals(""\r"", CodeGenerator.escapeToDoubleQuotedJsString("\r"));
    // vertical tab (0x0B) has no short form; must use a hex escape, not raw control char
    String vt = CodeGenerator.escapeToDoubleQuotedJsString("\013");
    assertFalse(vt.contains("\013")); // raw VT must not appear
    assertTrue(vt.charAt(vt.indexOf('\') + 1) != 'u' || vt.contains("\x0B") || vt.contains("\x0b"));
}

  public void testNormalAsciiStringPreserved() {
    String input = "hello world 123 !@#";
    String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
    assertTrue(result.contains("hello world 123 !@#")); }

  public void testMixedullAndNormalChars() {
    String result = CodeGenerator.escapeToDoubleQuotedJsString("A\0B\0C");
    assertTrue(result.contains("A\0B\0C")); }

  public void testSqlAndDoubleQuoteEscaping() {
    // Input with both quote types; method picks optimal quote
    String result = CodeGenerator.escapeToDoubleQuotedJsString("a"b'c");
    // Must be wrapped in something and inner quotes handled
    assertTrue(result.charAt(0) == '"' || result.charAt(0) == '''); }

  public void testIdentiferEscapeNull() {
    // Null in identifiers: should be hex-escaped (no \0 shorthand in identifiers)
    String result = CodeGenerator.identifierEscape("a\u0000b");
    assertTrue(result.contains("\u0000"));
    assertFalse(result.contains("\0a") || result.contains("a\0")); }

  public void testRegexpEscapeNull() {
    // Null in regex: should not leave raw null byte
    String result = CodeGenerator.regexpEscape("\0x");
    assertFalse(result.contains("\0")); // raw null must not survive
    assertTrue(result.contains("\0") || result.contains("\u0000") || result.contains("\x00")); }
}
