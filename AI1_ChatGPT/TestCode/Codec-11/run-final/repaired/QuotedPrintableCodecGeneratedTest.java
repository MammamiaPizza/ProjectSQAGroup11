package org.apache.commons.codec.net;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class QuotedPrintableCodecGeneratedTest {

    @Test
    public void testDefaultCharsetAndAsciiEncodingRoundTrip() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec();

        assertEquals("UTF-8", codec.getDefaultCharset());
        assertEquals("Simple=3Dvalue=00", codec.encode("Simple=value\u0000"));
        assertEquals("Simple=value\u0000", codec.decode("Simple=3Dvalue=00"));
    }

    @Test
    public void testSpecifiedCharsetEncodesAndDecodesMultibyteCharacters() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("UTF-8");

        assertEquals("caf=C3=A9", codec.encode("café", "UTF-8"));
        assertEquals("café", codec.decode("caf=C3=A9", "UTF-8"));
    }

    @Test
    public void testByteEncodingEscapesUnsafeCharactersAndPreservesPrintableCharacters() {
        QuotedPrintableCodec codec = new QuotedPrintableCodec();
        byte[] source = new byte[] { 'A', ' ', '\t', '=', 0, (byte) 0xFF };

        assertArrayEquals(
                new byte[] { 'A', ' ', '\t', '=', '3', 'D', '=', '0', '0', '=', 'F', 'F' },
                codec.encode(source));
    }

    @Test
    public void testStaticEncodingUsesProvidedPrintableCharacterSet() {
        BitSet printable = new BitSet(256);
        printable.set('A');

        byte[] encoded = QuotedPrintableCodec.encodeQuotedPrintable(
                printable, new byte[] { 'A', ' ', 'B' });

        assertArrayEquals(new byte[] { 'A', '=', '2', '0', '=', '4', '2' }, encoded);
    }

    @Test
    public void testStaticEncodingWithNullPrintableSetUsesDefaultPrintableCharacters() {
        byte[] encoded = QuotedPrintableCodec.encodeQuotedPrintable(
                null, new byte[] { 'A', '=', 0 });

        assertArrayEquals(new byte[] { 'A', '=', '3', 'D', '=', '0', '0' }, encoded);
    }

    @Test
    public void testDecodeDecodesEscapedOctetsAndSkipsUnescapedCrLf() throws Exception {
        byte[] decoded = QuotedPrintableCodec.decodeQuotedPrintable(
                "first\r\nsecond=0D=0Athird".getBytes("US-ASCII"));

        assertArrayEquals(
                "firstsecond\r\nthird".getBytes("US-ASCII"),
                decoded);
    }

    @Test
    public void testDecodeRemovesSoftLineBreak() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("US-ASCII");

        assertEquals("abcdef", codec.decode("abc=\r\ndef"));
        assertArrayEquals(
                "abcdef".getBytes("US-ASCII"),
                codec.decode("abc=\r\ndef".getBytes("US-ASCII")));
    }

    @Test
    public void testEncodeEncodesWhitespaceAtEndOfLinesAndAtEndOfData() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("US-ASCII");

        assertEquals("line=20=0D=0Anext=09", codec.encode("line \r\nnext\t"));
        assertEquals("line \r\nnext\t", codec.decode("line=20=0D=0Anext=09"));
    }

    @Test
    public void testEncodeLongLineUsesSoftLineBreakAndDecodesBackToOriginal() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("US-ASCII");
        String input = repeatedCharacter('A', 77);

        String encoded = codec.encode(input);

        assertTrue("Quoted-printable output must wrap a line longer than 76 characters",
                encoded.indexOf("=\r\n") >= 0);
        assertEquals(input, codec.decode(encoded));

        String[] physicalLines = encoded.split("\r\n", -1);
        for (int i = 0; i < physicalLines.length; i++) {
            assertTrue("Each encoded physical line must be at most 76 characters",
                    physicalLines[i].length() <= 76);
        }
    }

    @Test
    public void testDecodeRejectsIncompleteEscapeSequence() throws Exception {
        try {
            QuotedPrintableCodec.decodeQuotedPrintable("abc=".getBytes("US-ASCII"));
            fail("An incomplete quoted-printable escape must be rejected");
        } catch (DecoderException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void testDecodeRejectsNonHexEscapeSequence() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec();

        try {
            codec.decode("bad=QZ");
            fail("A quoted-printable escape must contain two hexadecimal digits");
        } catch (DecoderException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void testObjectEncodeAndDecodeDispatchForSupportedTypes() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("US-ASCII");

        Object encodedBytes = codec.encode((Object) new byte[] { '=', 0 });
        assertTrue(encodedBytes instanceof byte[]);
        assertArrayEquals(new byte[] { '=', '3', 'D', '=', '0', '0' }, (byte[]) encodedBytes);

        Object decodedBytes = codec.decode((Object) new byte[] { '=', '3', 'D', '=', '0', '0' });
        assertTrue(decodedBytes instanceof byte[]);
        assertArrayEquals(new byte[] { '=', 0 }, (byte[]) decodedBytes);

        assertEquals("a=3Db", codec.encode((Object) "a=b"));
        assertEquals("a=b", codec.decode((Object) "a=3Db"));
    }

    @Test
    public void testObjectEncodeAndDecodeRejectUnsupportedTypes() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec();

        try {
            codec.encode((Object) Integer.valueOf(1));
            fail("Non-string and non-byte-array objects cannot be encoded");
        } catch (EncoderException expected) {
            assertTrue(expected.getMessage().contains(Integer.class.getName()));
        }

        try {
            codec.decode((Object) Integer.valueOf(1));
            fail("Non-string and non-byte-array objects cannot be decoded");
        } catch (DecoderException expected) {
            assertTrue(expected.getMessage().contains(Integer.class.getName()));
        }
    }

    @Test
    public void testNullInputsReturnNull() throws Exception {
        QuotedPrintableCodec codec = new QuotedPrintableCodec();

        assertNull(QuotedPrintableCodec.encodeQuotedPrintable(null, null));
        assertNull(QuotedPrintableCodec.decodeQuotedPrintable(null));
        assertNull(codec.encode((byte[]) null));
        assertNull(codec.decode((byte[]) null));
        assertNull(codec.encode((String) null));
        assertNull(codec.decode((String) null));
        assertNull(codec.encode((Object) null));
        assertNull(codec.decode((Object) null));
        assertNull(codec.encode(null, "UTF-8"));
        assertNull(codec.decode(null, "UTF-8"));
    }

    @Test
    public void testUnsupportedDefaultCharsetIsReportedThroughCodecExceptions() {
        QuotedPrintableCodec codec = new QuotedPrintableCodec("definitely-not-a-java-charset");

        try {
            codec.encode("text");
            fail("Encoding with an unsupported default charset must fail");
        } catch (EncoderException expected) {
            assertTrue(expected.getCause() instanceof UnsupportedEncodingException);
        }

        try {
            codec.decode("text");
            fail("Decoding with an unsupported default charset must fail");
        } catch (DecoderException expected) {
            assertTrue(expected.getCause() instanceof UnsupportedEncodingException);
        }
    }

    private static String repeatedCharacter(char character, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }
}
