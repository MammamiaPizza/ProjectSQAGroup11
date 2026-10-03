package org.apache.commons.csv;

 import static org.apache.commons.csv.Constants.*;
 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringReader;

 import org.junit.Test;

 /**
  * Tests {@link Lexer} escape handling, quoting, and boundary behaviour,
  * targeting the CSV-58 backslash-escape regression.
  */
 public class LexerTest {

     /** Minimal concrete lexer to exercise the abstract methods under test. */
     private static final class TestLexer extends Lexer {
         TestLexer(final CSVFormat format, final ExtendedBufferedReader in) {
             super(format, in);
         }

         @Override
         Token nextToken(final Token reusableToken) throws IOException {
             return null;
         }
     }

     // --------------- Helpers ---------------

     private static Lexer lexerFor(final CSVFormat format, final String input) {
         final ExtendedBufferedReader reader =
                 new ExtendedBufferedReader(new java.io.BufferedReader(new StringReader(input)));
         return new TestLexer(format, reader);
     }

     private static Lexer lexerWithEsc(final String input) {
         // escape '\\', delimiter ',' (not used), quote '"' (not used)
         final CSVFormat format = CSVFormat.DEFAULT
                 .withEscape(Character.valueOf('\\'));
         return lexerFor(format, input);
     }

     // --------------- readEscape tests ---------------

     @Test
     public void testReadEscape_SpecialSequences() throws IOException {
         final Lexer lexer = lexerWithEsc("rntbf");
         assertEquals(CR, lexer.readEscape());   // \r
         assertEquals(LF, lexer.readEscape());   // \n
         assertEquals(TAB, lexer.readEscape());  // \t
         assertEquals(BACKSPACE, lexer.readEscape()); // \b
         assertEquals(FF, lexer.readEscape());   // \f
     }

     @Test
     public void testReadEscape_ControlCharsPassThrough() throws IOException {
         final Lexer lexer = lexerWithEsc(
                 String.valueOf((char) CR) + (char) LF + (char) FF + (char) TAB + (char) BACKSPACE);
         assertEquals(CR, lexer.readEscape());
         assertEquals(LF, lexer.readEscape());
         assertEquals(FF, lexer.readEscape());
         assertEquals(TAB, lexer.readEscape());
         assertEquals(BACKSPACE, lexer.readEscape());
     }

     @Test
     public void testReadEscape_OrdinaryCharsPassThrough() throws IOException {
         final Lexer lexer = lexerWithEsc("x1$ ");
         assertEquals('x', lexer.readEscape());
         assertEquals('1', lexer.readEscape());
         assertEquals('$', lexer.readEscape());
         assertEquals(' ', lexer.readEscape());
     }

     @Test
     public void testReadEscape_BackslashReturnsBackslash() throws IOException {
         final Lexer lexer = lexerWithEsc("\\");
         assertEquals('\\', lexer.readEscape());
     }

     @Test
     public void testReadEscape_QuoteCharReturnsQuote() throws IOException {
         // The quote character itself is still returned, leaving quoting logic to the caller.
         final Lexer lexer = lexerWithEsc("\"");
         assertEquals('"', lexer.readEscape());
     }

     @Test
     public void testReadEscape_MySqlNullIsPlainN() throws IOException {
         // Bug CSV-58: \N should map to a null-like token, but currently returns 'N'.
         // This test captures the current (buggy) behaviour; it will pass on the buggy version
         // but should fail after the fix to signal the required change.
         final Lexer lexer = lexerWithEsc("N");
         assertEquals('N', lexer.readEscape());
     }

     @Test(expected = IOException.class)
     public void testReadEscape_EndOfStreamThrows() throws IOException {
         final Lexer lexer = lexerWithEsc("");
         lexer.readEscape();
     }

     // --------------- Character classification ---------------

     @Test
     public void testIsEscape() {
         final Lexer lexer = lexerFor(CSVFormat.DEFAULT.withEscape(Character.valueOf('\\')), "");
         assertTrue(lexer.isEscape('\\'));
         assertFalse(lexer.isEscape('n'));
         assertFalse(lexer.isEscape(END_OF_STREAM));
     }

     @Test
     public void testIsQuoteChar() {
         final Lexer lexer = lexerFor(CSVFormat.DEFAULT.withQuoteChar('"'), "");
         assertTrue(lexer.isQuoteChar('"'));
         assertFalse(lexer.isQuoteChar('\\'));
         assertFalse(lexer.isQuoteChar(END_OF_STREAM));
     }

     @Test
     public void testIsDelimiter() {
         final Lexer lexer = lexerFor(CSVFormat.DEFAULT.withDelimiter(','), "");
         assertTrue(lexer.isDelimiter(','));
         assertFalse(lexer.isDelimiter(';'));
     }

     // --------------- readEndOfLine ---------------

     @Test
     public void testReadEndOfLine_SingleAndCRLF() throws IOException {
         Lexer lexer = lexerFor(CSVFormat.DEFAULT, String.valueOf((char) CR));
         assertTrue(lexer.readEndOfLine(CR));

         lexer = lexerFor(CSVFormat.DEFAULT, String.valueOf((char) LF) + "x");
         // readEndOfLine(LF) should consume only the LF, not x
         ExtendedBufferedReader r = new ExtendedBufferedReader(
                 new java.io.BufferedReader(new StringReader(String.valueOf((char) LF) + "x")));
         Lexer l = new TestLexer(CSVFormat.DEFAULT, r);
         assertTrue(l.readEndOfLine(LF));
         assertEquals('x', r.read());

         lexer = lexerFor(CSVFormat.DEFAULT, String.valueOf((char) CR) + (char) LF);
         assertTrue(lexer.readEndOfLine(CR));
         assertEquals(2, lexer.getLineNumber()); // CRLF increments line twice
     }

     // --------------- Utility methods ---------------

     @Test
     public void testTrimTrailingSpaces() {
         final Lexer lexer = lexerFor(CSVFormat.DEFAULT, "");
         final StringBuilder sb1 = new StringBuilder("a b  ");
         lexer.trimTrailingSpaces(sb1);
         assertEquals("a b", sb1.toString());

         final StringBuilder sb2 = new StringBuilder("no-spaces");
         lexer.trimTrailingSpaces(sb2);
         assertEquals("no-spaces", sb2.toString());

         final StringBuilder sb3 = new StringBuilder("   ");
         lexer.trimTrailingSpaces(sb3);
         assertEquals("", sb3.toString());
     }
 }
