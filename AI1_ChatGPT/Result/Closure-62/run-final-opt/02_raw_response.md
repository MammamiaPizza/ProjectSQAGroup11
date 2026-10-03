package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LightweightMessageFormatterTrailingSpaceTest {

  private static final String DESCRIPTION = "error description here";
  private static final DiagnosticType TYPE = DiagnosticType.error(DESCRIPTION);

  @Test
  public void testFormatErrorSpaceEndOfLine1() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "line 2 "));

    assertEquals(
        "test.js:1: ERROR - error description here\n"
            + "line 2 \n"
            + "       ^\n",
        formatter.formatError(error("test.js", 1, 7)));
  }

  @Test
  public void testFormatErrorSpaceEndOfLine2() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "line 2  "));

    assertEquals(
        "test.js:1: ERROR - error description here\n"
            + "line 2  \n"
            + "        ^\n",
        formatter.formatError(error("test.js", 1, 8)));
  }

  @Test
  public void testFormatErrorEndOfLineWithoutTrailingSpaceShowsCaret() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "abc"));

    assertEquals(
        "test.js:1: ERROR - error description here\n"
            + "abc\n"
            + "   ^\n",
        formatter.formatError(error("test.js", 1, 3)));
  }

  @Test
  public void testFormatErrorCaretAtTrailingWhitespacePreservesWhitespacePadding() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "abc\t"));

    assertEquals(
        "test.js:1: ERROR - error description here\n"
            + "abc\t\n"
            + "   ^\n",
        formatter.formatError(error("test.js", 1, 3)));
  }

  @Test
  public void testFormatWarningAtEndOfTrailingSpaceLineShowsCaret() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "x "));

    assertEquals(
        "test.js:1: WARNING - error description here\n"
            + "x \n"
            + "  ^\n",
        formatter.formatWarning(error("test.js", 1, 2)));
  }

  @Test
  public void testFormatErrorWithInvalidNegativeCharacterNumberHasNoCaret() {
    LightweightMessageFormatter formatter =
        new LightweightMessageFormatter(new SingleLineSource("test.js", "abc"));

    assertEquals(
        "test.js:1: ERROR - error description here\n"
            + "abc\n",
        formatter.formatError(error("test.js", 1, -1)));
  }

  @Test
  public void testFormatErrorWithoutSourceContainsOnlyMessage() {
    assertEquals(
        "ERROR - error description here\n",
        LightweightMessageFormatter.withoutSource().formatError(error(null, -1, -1)));
  }

  private static JSError error(String sourceName, int lineNumber, int charno) {
    return JSError.make(sourceName, lineNumber, charno, TYPE);
  }

  private static final class SingleLineSource implements SourceExcerptProvider {
    private final String sourceName;
    private final String line;

    SingleLineSource(String sourceName, String line) {
      this.sourceName = sourceName;
      this.line = line;
    }

    @Override
    public String getSourceLine(String requestedSourceName, int lineNumber) {
      return sourceName.equals(requestedSourceName) && lineNumber == 1 ? line : null;
    }

    @Override
    public Region getSourceRegion(String requestedSourceName, int lineNumber) {
      return null;
    }
  }
}