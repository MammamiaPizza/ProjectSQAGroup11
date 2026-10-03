package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class Base32GeneratedTest {

    private static byte[] ascii(final String value) {
        try {
            return value.getBytes("US-ASCII");
        } catch (final UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    @Test
    public void testDefaultBase32Rfc4648VectorsEncodeAndDecode() {
        final Base32 codec = new Base32();

        final String[] plainText = { "", "f", "fo", "foo", "foob", "fooba", "foobar" };
        final String[] encodedText = {
                "",
                "MY======",
                "MZXQ====",
                "MZXW6===",
                "MZXW6YQ=",
                "MZXW6YTB",
                "MZXW6YTBOI======"
        };

        for (int i = 0; i < plainText.length; i++) {
            assertArrayEquals("Unexpected Base32 encoding for " + plainText[i],
                    ascii(encodedText[i]), codec.encode(ascii(plainText[i])));
            assertArrayEquals("Unexpected Base32 decoding for " + encodedText[i],
                    ascii(plainText[i]), codec.decode(ascii(encodedText[i])));
        }
    }

    @Test
    public void testDecodeAcceptsOptionalPaddingAndIgnoresNonAlphabetCharacters() {
        final Base32 codec = new Base32();

        assertArrayEquals(ascii("foo"), codec.decode(ascii("MZ!XW6===")));
        assertArrayEquals(ascii("foo"), codec.decode(ascii(" MZ XW6===\r\n")));
        assertArrayEquals(ascii("f"), codec.decode(ascii("MZX")));
        assertArrayEquals(ascii("foo"), codec.decode(ascii("MZXW6Y")));
        assertArrayEquals(new byte[0], codec.decode(ascii("M")));
    }

    @Test
    public void testBase32HexUsesRfc4648HexAlphabet() {
        final Base32 codec = new Base32(true);

        assertArrayEquals(ascii("CPNMUOJ1E8======"), codec.encode(ascii("foobar")));
        assertArrayEquals(ascii("foobar"), codec.decode(ascii("CPNMUOJ1E8======")));

        assertTrue(codec.isInAlphabet((byte) '0'));
        assertTrue(codec.isInAlphabet((byte) 'V'));
        assertFalse(codec.isInAlphabet((byte) 'W'));
    }

    @Test
    public void testBase32HexAllowsWAsCustomPaddingBecauseItIsNotInHexAlphabet() {
        final Base32 codec = new Base32(true, (byte) 'W');

        assertArrayEquals(ascii("COWWWWWW"), codec.encode(ascii("f")));
        assertArrayEquals(ascii("f"), codec.decode(ascii("COWWWWWW")));
    }

    @Test
    public void testRejectsPaddingThatIsAlphabetCharacterOrWhitespace() {
        assertInvalidPad(false, (byte) 'A');
        assertInvalidPad(false, (byte) ' ');
        assertInvalidPad(true, (byte) 'V');
    }

    @Test
    public void testChunkedEncodingRoundsLineLengthDownToBase32BlockSize() {
        final Base32 codec = new Base32(10);
        final byte[] input = new byte[10];

        assertArrayEquals(ascii("AAAAAAAA\r\nAAAAAAAA\r\n"), codec.encode(input));
        assertArrayEquals(input, codec.decode(ascii("AAAAAAAA\r\nAAAAAAAA\r\n")));
    }

    @Test
    public void testRejectsInvalidChunkingConfiguration() {
        try {
            new Base32(8, ascii("A"));
            fail("A line separator containing a Base32 alphabet character must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("lineSeparator"));
        }

        try {
            new Base32(8, null, false);
            fail("A positive line length requires a line separator");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("lineSeparator"));
        }
    }

    @Test
    public void testZeroLineLengthIgnoresProvidedSeparatorAndDoesNotChunkOutput() {
        final Base32 codec = new Base32(0, ascii("A"), false, (byte) '!');

        assertArrayEquals(ascii("MY!!!!!!"), codec.encode(ascii("f")));
        assertArrayEquals(ascii("f"), codec.decode(ascii("MY!!!!!!")));
    }

    @Test
    public void testRoundTripPreservesUnsignedAndNegativeByteValues() {
        final Base32 codec = new Base32();
        final byte[] input = { 0, (byte) 0xff, 0x10, (byte) 0x80, 0x7f };

        assertArrayEquals(input, codec.decode(codec.encode(input)));
    }

    @Test
    public void testNullInputIsReturnedAsNull() {
        final Base32 codec = new Base32();

        assertNull(codec.encode((byte[]) null));
        assertNull(codec.decode((byte[]) null));
    }

    private static void assertInvalidPad(final boolean useHex, final byte pad) {
        try {
            new Base32(useHex, pad);
            fail("Padding byte should be rejected: " + pad);
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pad"));
        }
    }
}
