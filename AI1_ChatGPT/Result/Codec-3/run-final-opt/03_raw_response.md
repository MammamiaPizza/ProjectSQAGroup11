package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class DoubleMetaphoneCodec3BugTest {

    @Test
    public void alternateEncodingOfAngierUsesJNotK() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("ANJR", encoder.doubleMetaphone("Angier", true));
    }

    @Test
    public void primaryAndDefaultEncodingOfAngierAreANKR() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("ANKR", encoder.doubleMetaphone("Angier"));
        assertEquals("ANKR", encoder.doubleMetaphone("Angier", false));
    }

    @Test
    public void alternateEncodingOfAngierUsesJWhilePrimaryUsesK() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("ANKR", encoder.doubleMetaphone("Angier"));
        assertEquals("ANJR", encoder.doubleMetaphone("Angier", true));
    }

    @Test
    public void nullInputProducesNullForBothOverloads() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertNull(encoder.doubleMetaphone(null));
        assertNull(encoder.doubleMetaphone(null, true));
    }

    @Test
    public void emptyAndWhitespaceOnlyInputProduceNull() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertNull(encoder.doubleMetaphone(""));
        assertNull(encoder.doubleMetaphone("   ", true));
    }

    @Test
    public void maxCodeLengthTruncatesBothEncodings() {
        DoubleMetaphone encoder = new DoubleMetaphone();
        encoder.setMaxCodeLen(2);

        assertEquals(2, encoder.getMaxCodeLen());
        assertEquals("AN", encoder.doubleMetaphone("Angier"));
        assertEquals("AN", encoder.doubleMetaphone("Angier", true));
    }

    @Test
    public void stringEncodeDelegatesToPrimaryEncoding() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals(encoder.doubleMetaphone("Angier"), encoder.encode("Angier"));
    }

    @Test
    public void objectEncodeRejectsNonStringValues() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        try {
            encoder.encode(Integer.valueOf(19));
            fail("Non-String values must be rejected");
        } catch (EncoderException expected) {
            assertTrue(expected.getMessage().contains("not of type String"));
        }
    }
}