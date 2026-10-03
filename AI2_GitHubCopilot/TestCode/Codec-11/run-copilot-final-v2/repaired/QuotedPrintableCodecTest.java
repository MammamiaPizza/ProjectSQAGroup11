package org.apache.commons.codec.net;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**

 - Tests for QuotedPrintableCodec targeting the CODEC-121 issues (soft line breaks,
 - trailing specials, CR/LF handling).
   */
  public class QuotedPrintableCodecTest {
   private QuotedPrintableCodec codec;
   @Before
   public void setUp() {
   codec = new QuotedPrintableCodec();
   }
   // ---- decode: soft line break handling ----
   @Test
   public void testDecodeSoftBreakCRLF() throws Exception {
   // "=\r\n" must be stripped silently, not cause a DecoderException
   byte[] input = "Hello=\r\nWorld".getBytes("US-ASCII");
   byte[] expected = "HelloWorld".getBytes("US-ASCII");
   byte[] result = QuotedPrintableCodec.decodeQuotedPrintable(input);
   assertArrayEquals(expected, result);
   }
   @Test
   public void testDecodeSoftBreakAtEnd() throws Exception {
   byte[] input = "Data=\r\n".getBytes("US-ASCII");
   byte[] expected = "Data".getBytes("US-ASCII");
   byte[] result = QuotedPrintableCodec.decodeQuotedPrintable(input);
   assertArrayEquals(expected, result);
   }
   @Test
   public void testDecodeMultipleSoftBreaks() throws Exception {
   byte[] input = "A=\r\nB=\r\nC".getBytes("US-ASCII");
   byte[] expected = "ABC".getBytes("US-ASCII");
   byte[] result = QuotedPrintableCodec.decodeQuotedPrintable(input);
   assertArrayEquals(expected, result);
   }
   @Test(expected = DecoderException.class)
   public void testDecodeInvalidHexThrowsException() throws Exception {
   // 'Z' is not a valid hex digit
   byte[] input = "=ZZ".getBytes("US-ASCII");
   QuotedPrintableCodec.decodeQuotedPrintable(input);
   }
   @Test(expected = DecoderException.class)
   public void testDecodeTruncatedEscapeThrowsException() throws Exception {
   // not enough bytes after '='
   byte[] input = "=1".getBytes("US-ASCII");
   QuotedPrintableCodec.decodeQuotedPrintable(input);
   }
   @Test
   public void testDecodePreserveCRLF() throws Exception {
   // Plain CRLF (without leading '=') is treated as a not-encoded line break
   // and is skipped by decodeQuotedPrintable.
   byte[] input = "Line1\r\nLine2".getBytes("US-ASCII");
   byte[] expected = "Line1Line2".getBytes("US-ASCII");
   byte[] result = QuotedPrintableCodec.decodeQuotedPrintable(input);
   assertArrayEquals(expected, result);
   }
   // ---- encode: soft line breaks and trailing specials ----
   @Test
   public void testEncodeSoftLineBreaksMaxLineLength() throws Exception {
   // A long string of printable chars must be broken with "=\r\n" sequences
   String original = repeat('A', 200);
   byte[] encoded = codec.encode(original.getBytes("US-ASCII"));
   // Verify every line (between CRLF) has length <= 76 and non-final lines end with '='
   int start = 0;
   for (int i = 0; i < encoded.length; i++) {
    if (i + 1 < encoded.length && encoded[i] == '\r' && encoded[i + 1] == '\n') {
    int lineLen = i - start;
    assertTrue("Line too long: " + lineLen, lineLen <= 76);
    if (i + 2 < encoded.length) { // not the very last line
        assertEquals('=', encoded[start + lineLen - 1]);
    }
    start = i + 2; // skip CRLF
    i++; // advance past LF
    }
   }
   // last segment length check
   int lastLen = encoded.length - start;
   assertTrue("Last line too long: " + lastLen, lastLen <= 76);
   // Round-trip: decode must give original
   byte[] decoded = codec.decode(encoded);
   assertArrayEquals(original.getBytes("US-ASCII"), decoded);
   }
   @Test
   public void testEncodeTrailingSpace() throws Exception {
   // Trailing space must be encoded as "=20"
   String input = "trailing space ";
   String encoded = codec.encode(input);
   assertEquals("trailing space=20", encoded);
   }
   @Test
   public void testEncodeTrailingTab() throws Exception {
   // Trailing tab must be encoded as "=09"
   String input = "trailing tab\t";
   String encoded = codec.encode(input);
   assertEquals("trailing tab=09", encoded);
   }
   @Test
   public void testEncodeTrailingEquals() throws Exception {
   // Trailing '=' must be encoded as "=3D" to avoid ambiguity
   String input = "trailing equals=";
   String encoded = codec.encode(input);
   assertEquals("trailing equals=3D", encoded);
   }
   @Test
   public void testRoundTripSimple() throws Exception {
   String original = "Hello World! 12345";
   byte[] encoded = codec.encode(original.getBytes("US-ASCII"));
   byte[] decoded = codec.decode(encoded);
   assertArrayEquals(original.getBytes("US-ASCII"), decoded);
   }
   @Test
   public void testEncodeShortPrintableNoSoftBreak() throws Exception {
   // Short printable string must be returned unchanged (no soft breaks, no encoding)
   String input = "Short ASCII text";
   String encoded = codec.encode(input);
   assertEquals(input, encoded);
   }
   // helper
   private static String repeat(char ch, int count) {
   StringBuilder sb = new StringBuilder(count);
   for (int i = 0; i < count; i++) {
    sb.append(ch);
   }
   return sb.toString();
   }

}
