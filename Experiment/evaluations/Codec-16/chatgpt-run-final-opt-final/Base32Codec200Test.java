package org.apache.commons.codec.binary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class Base32Codec200Test {

    @Test
    public void defaultConstructorCreatesCodecWithNonAlphabetDefaultPad() {
        Base32 codec = new Base32();

        assertNotNull(codec);
        assertFalse(codec.isInAlphabet((byte) '='));
    }

    @Test
    public void bytePadConstructorAcceptsNonAlphabetNonWhitespacePad() {
        Base32 codec = new Base32((byte) '!');

        assertNotNull(codec);
        assertFalse(codec.isInAlphabet((byte) '!'));
    }

    @Test
    public void constructorRejectsStandardBase32AlphabetCharacterAsPad() {
        try {
            new Base32(false, (byte) 'A');
            fail("A Base32 alphabet character must not be used as padding");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pad must not be in alphabet or whitespace"));
        }
    }

    @Test
    public void bytePadConstructorRejectsWhitespaceAsPad() {
        try {
            new Base32((byte) ' ');
            fail("Whitespace must not be used as padding");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pad must not be in alphabet or whitespace"));
        }
    }

    @Test
    public void constructorRejectsBase32HexAlphabetCharacterAsPad() {
        try {
            new Base32(true, (byte) '0');
            fail("A Base32 Hex alphabet character must not be used as padding");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pad must not be in alphabet or whitespace"));
        }
    }

    @Test
    public void fullConstructorRejectsAlphabetPadWithLineConfiguration() {
        try {
            new Base32(8, new byte[] { '\r', '\n' }, false, (byte) 'Z');
            fail("A Base32 alphabet character must not be used as padding");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pad must not be in alphabet or whitespace"));
        }
    }

    @Test
    public void positiveLineLengthWithoutSeparatorIsRejected() {
        try {
            new Base32(8, null, false, (byte) '!');
            fail("A positive line length requires a line separator");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("lineSeparator"));
        }
    }

    @Test
    public void lineSeparatorContainingBase32CharacterIsRejected() {
        try {
            new Base32(8, new byte[] { 'A' }, false, (byte) '!');
            fail("A line separator must not contain Base32 alphabet characters");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("lineSeparator"));
        }
    }

@org.junit.Test
public void lineLengthConstructorEncodesChunksUsingDefaultSeparator() {
    final org.apache.commons.codec.binary.Base32 codec = new org.apache.commons.codec.binary.Base32(8);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'N', 'B', 'S', 'W', 'Y', '3', 'D', 'P', '\r', '\n' },
            codec.encode(new byte[] { 'h', 'e', 'l', 'l', 'o' }));
}

@org.junit.Test
public void customLineSeparatorIsUsedWhenEncodingChunks() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32(8, new byte[] { '!' });

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'N', 'B', 'S', 'W', 'Y', '3', 'D', 'P', '!' },
            codec.encode(new byte[] { 'h', 'e', 'l', 'l', 'o' }));
}

@org.junit.Test
public void base32HexConstructorEncodesAndDecodesUsingHexAlphabet() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32(0, null, true);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'C', 'P', 'N', 'M', 'U', '=', '=', '=' },
            codec.encode(new byte[] { 'f', 'o', 'o' }));
    org.junit.Assert.assertArrayEquals(
            new byte[] { 'f', 'o', 'o' },
            codec.decode("CPNMU==="));
}

@org.junit.Test
public void customPadIsUsedForEncodingAndDecoding() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32((byte) '!');

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'M', 'Z', 'X', 'W', '6', '!', '!', '!' },
            codec.encode(new byte[] { 'f', 'o', 'o' }));
    org.junit.Assert.assertArrayEquals(
            new byte[] { 'f', 'o', 'o' },
            codec.decode("MZXW6!!!"));
}
}
