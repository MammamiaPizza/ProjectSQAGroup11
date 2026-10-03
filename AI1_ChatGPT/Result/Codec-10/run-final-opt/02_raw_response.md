package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class CaverphoneDefectsTest {

    @Test
    public void testRepeatedTerminalMbIsProcessedOnlyAtEnd() {
        Caverphone caverphone = new Caverphone();

        String code = caverphone.caverphone("mbmb");

        assertEquals("MPM1111111", code);
        assertEquals(10, code.length());
    }

    @Test
    public void testSingleTerminalMb() {
        assertEquals("M111111111", new Caverphone().caverphone("mb"));
    }

    @Test
    public void testTerminalMbAfterOtherCharacters() {
        assertEquals("KM11111111", new Caverphone().caverphone("xmb"));
    }

    @Test
    public void testInitialNonTerminalMbIsNotTreatedAsTerminal() {
        assertEquals("MPK1111111", new Caverphone().caverphone("mbx"));
    }

    @Test
    public void testTerminalMbRuleIsCaseInsensitive() {
        assertEquals("MPM1111111", new Caverphone().caverphone("MBMB"));
    }

    @Test
    public void testNullAndEmptyInputsProducePaddedCode() {
        Caverphone caverphone = new Caverphone();

        assertEquals("1111111111", caverphone.caverphone(null));
        assertEquals("1111111111", caverphone.caverphone(""));
    }

    @Test
    public void testStringAndObjectEncodeDelegateToCaverphone() throws EncoderException {
        Caverphone caverphone = new Caverphone();

        assertEquals("MPM1111111", caverphone.encode("mbmb"));
        assertEquals("MPM1111111", caverphone.encode((Object) "mbmb"));
        assertEquals("1111111111", caverphone.encode((String) null));
    }

    @Test
    public void testObjectEncodeRejectsNonString() {
        try {
            new Caverphone().encode(Integer.valueOf(1));
            fail("Non-String objects must be rejected");
        } catch (EncoderException expected) {
            assertTrue(expected.getMessage().contains("java.lang.String"));
        }
    }

    @Test
    public void testIsCaverphoneEqualUsesCorrectedEncoding() {
        Caverphone caverphone = new Caverphone();

        assertTrue(caverphone.isCaverphoneEqual("mbmb", "mpm"));
        assertFalse(caverphone.isCaverphoneEqual("mbmb", "xmb"));
    }
}