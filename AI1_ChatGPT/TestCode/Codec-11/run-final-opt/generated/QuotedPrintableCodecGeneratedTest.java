package org.apache.commons.codec.net;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class QuotedPrintableCodecGeneratedTest {

    @Test
    public void decodesLiteralCrLfWithoutTreatingItAsAnEscape() throws Exception {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        assertEquals("first\r\nsecond", codec.decode("first\r\nsecond"));
    }

    @Test
    public void decodesSoftLineBreakByRemovingEqualCrLf() throws Exception {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        assertEquals("firstsecond", codec.decode("first=\r\nsecond"));
    }

    @Test
    public void decodesEscapedBytesAndPreservesOtherLiteralBytes() throws Exception {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        assertEquals("a=b\r\nc", codec.decode("a=3Db\r\nc"));
    }

    @Test
    public void encodesTrailingWhitespaceAtEndOfLine() {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();

        assertEquals("text=20", new String(codec.encode("text ".getBytes(Charset.forName("US-ASCII"))),
                Charset.forName("US-ASCII")));
        assertEquals("text=09", new String(codec.encode("text\t".getBytes(Charset.forName("US-ASCII"))),
                Charset.forName("US-ASCII")));
    }

    @Test
    public void preservesHardCrLfWhenEncoding() {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        assertEquals("one\r\ntwo", new String(codec.encode("one\r\ntwo".getBytes(Charset.forName("US-ASCII"))),
                Charset.forName("US-ASCII")));
    }

    @Test
    public void encodesSpecialEqualsCharacterIncludingAtEnd() {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        assertEquals("value=3D", new String(codec.encode("value=".getBytes(Charset.forName("US-ASCII"))),
                Charset.forName("US-ASCII")));
    }

    @Test
    public void insertsSoftBreaksForLongLinesAndRoundTripsThem() throws Exception {
        final QuotedPrintableCodec codec = new QuotedPrintableCodec();
        final String source = repeated('A', 100) + "=";
        final String encoded = new String(codec.encode(source.getBytes(Charset.forName("US-ASCII"))),
                Charset.forName("US-ASCII"));

        assertTrue(encoded.contains("=\r\n"));
        assertTrue(encoded.endsWith("=3D"));
        for (final String line : encoded.split("\\r\\n", -1)) {
            assertTrue("quoted-printable physical line exceeds 76 characters", line.length() <= 76);
        }
        assertEquals(source, codec.decode(encoded));
    }

    @Test
    public void staticEncodingHonorsProvidedPrintableSet() {
        final BitSet printable = new BitSet(256);
        printable.set('A');

        assertArrayEquals("A=42".getBytes(Charset.forName("US-ASCII")),
                QuotedPrintableCodec.encodeQuotedPrintable(printable,
                        "AB".getBytes(Charset.forName("US-ASCII"))));
    }

    @Test(expected = DecoderException.class)
    public void rejectsIncompleteEscapeSequence() throws Exception {
        QuotedPrintableCodec.decodeQuotedPrintable("=".getBytes(Charset.forName("US-ASCII")));
    }

    private static String repeated(final char character, final int count) {
        final StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }
}
