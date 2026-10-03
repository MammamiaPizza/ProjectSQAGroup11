package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**

 - Tests for CSVFormat escaping with null quote character.
 - Follows bug CSV-171: when escape is set but quote is null, values should not be quoted.
  */
 public class CSVFormatTest {
  private static final char ESC = '\';
  private static final char DELIM = ',';
  private static final char QUOTE = '"';
  // --- Escape set, quote null ---
  @Test
  public void testEscapeWithNullQuote_EscapeCharOnly() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("\", fmt.format("\"));
  }
  @Test
  public void testEscapeWithNullQuote_EscapeCharInMiddle() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("a\b", fmt.format("a\b"));
  }
  @Test
  public void testEscapeWithNullQuote_EscapeCharAtStart() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("\abc", fmt.format("\abc"));
  }
  @Test
  public void testEscapeWithNullQuote_EscapeCharAtEnd() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("abc\", fmt.format("abc\"));
  }
  @Test
  public void testEscapeWithNullQuote_EmptyValue() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("", fmt.format(""));
  }
  @Test
  public void testEscapeWithNullQuote_NullValue() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null).withNullString("N/A");
  assertEquals("N/A", fmt.format(new Object[] { (Object) null }));
  }
  @Test
  public void testEscapeWithNullQuote_DelimiterInValue() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("a,b", fmt.format("a,b"));
  }
  @Test
  public void testEscapeWithNullQuote_EscapeCharEqualsDelimiter() {
  // escape and delimiter are the same character
  CSVFormat fmt = CSVFormat.newFormat('\').withEscape('\').withQuote(null);
  assertEquals("a\b", fmt.format("a\b"));
  }
  @Test
  public void testEscapeWithNullQuote_ValueWithLineBreak() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("line1\nline2", fmt.format("line1\nline2"));
  }
  @Test
  public void testEscapeWithNullQuote_MultipleEscapeChars() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(ESC).withQuote(null);
  assertEquals("\\", fmt.format("\\"));
  }
  // --- Escape and quote both null ---
  @Test
  public void testEscapeAndQuoteNull_OutputRaw() {
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withEscape(null).withQuote(null);
  assertEquals("a,b", fmt.format("a,b"));
  }
  // --- Normal quoting (quote set, escape not) ---
  @Test
  public void testWithQuote_NoEscape_ValueWithDelimiter() {
  // Normal quoting: delimiter in value forces quoting
  CSVFormat fmt = CSVFormat.newFormat(DELIM).withQuote(QUOTE).withEscape(null);
  assertEquals(""a,b"", fmt.format("a,b"));
  }

}
