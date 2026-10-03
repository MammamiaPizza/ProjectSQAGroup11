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
    public void constructorAcceptsStandardBase32AlphabetCharacterAsPad() {
        Base32 codec = new Base32(false, (byte) 'A');

        assertNotNull(codec);
        assertTrue(codec.isInAlphabet((byte) 'A'));
    }

    @Test
    public void bytePadConstructorAcceptsWhitespaceAsPad() {
        Base32 codec = new Base32((byte) ' ');

        assertNotNull(codec);
        assertFalse(codec.isInAlphabet((byte) ' '));
    }

    @Test
    public void constructorAcceptsBase32HexAlphabetCharacterAsPad() {
        Base32 codec = new Base32(true, (byte) '0');

        assertNotNull(codec);
        assertTrue(codec.isInAlphabet((byte) '0'));
    }

    @Test
    public void fullConstructorAcceptsAlphabetPadWithLineConfiguration() {
        Base32 codec = new Base32(8, new byte[] { '\r', '\n' }, false, (byte) 'Z');

        assertNotNull(codec);
        assertTrue(codec.isInAlphabet((byte) 'Z'));
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
}
