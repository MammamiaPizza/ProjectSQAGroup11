package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class DoubleMetaphoneGeneratedTest {

    @Test
    public void testAlternateEncodingForAngierUsesJAsReportedByCodec84() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("ANJR", encoder.doubleMetaphone("Angier", true));
    }

    @Test
    public void testPrimaryEncodingForAngierAlsoUsesJForIerSequence() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("ANJR", encoder.doubleMetaphone("Angier"));
    }

    @Test
    public void testInputIsTrimmedCaseInsensitiveAndBlankOrNullProducesNull() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals(encoder.doubleMetaphone("Angier"),
                encoder.doubleMetaphone("  aNgIeR  "));
        assertNull(encoder.doubleMetaphone(null));
        assertNull(encoder.doubleMetaphone(" \t \n "));
        assertNull(encoder.encode((String) null));
    }

    @Test
    public void testSilentStartProducesSameEncodingAsPronouncedWord() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals(encoder.doubleMetaphone("night"),
                encoder.doubleMetaphone("Knight"));
        assertTrue(encoder.isDoubleMetaphoneEqual("Knight", "night"));
    }

    @Test
    public void testEncodeObjectAcceptsStringsAndRejectsOtherObjects() throws Exception {
        DoubleMetaphone encoder = new DoubleMetaphone();

        Object encoded = encoder.encode((Object) "Angier");
        assertTrue(encoded instanceof String);
        assertEquals(encoder.doubleMetaphone("Angier"), encoded);

        try {
            encoder.encode((Object) Integer.valueOf(42));
            fail("Encoding a non-String object must throw EncoderException");
        } catch (EncoderException expected) {
            assertEquals("DoubleMetaphone encode parameter is not of type String",
                    expected.getMessage());
        }

        try {
            encoder.encode((Object) null);
            fail("Encoding a null Object must throw EncoderException");
        } catch (EncoderException expected) {
            assertEquals("DoubleMetaphone encode parameter is not of type String",
                    expected.getMessage());
        }
    }

    @Test
    public void testEqualitySupportsPrimaryAndAlternateModes() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertTrue(encoder.isDoubleMetaphoneEqual("Angier", " angier "));
        assertTrue(encoder.isDoubleMetaphoneEqual("Angier", "ANGIER", true));
        assertFalse(encoder.isDoubleMetaphoneEqual("A", "B"));
    }

    @Test
    public void testMaximumCodeLengthLimitsBothEncodings() {
        DoubleMetaphone encoder = new DoubleMetaphone();
        String defaultPrimary = encoder.doubleMetaphone("Washington");
        String defaultAlternate = encoder.doubleMetaphone("Washington", true);

        assertEquals(4, encoder.getMaxCodeLen());

        encoder.setMaxCodeLen(2);
        assertEquals(2, encoder.getMaxCodeLen());
        assertEquals(defaultPrimary.substring(0, Math.min(2, defaultPrimary.length())),
                encoder.doubleMetaphone("Washington"));
        assertEquals(defaultAlternate.substring(0, Math.min(2, defaultAlternate.length())),
                encoder.doubleMetaphone("Washington", true));

        encoder.setMaxCodeLen(0);
        assertEquals("", encoder.doubleMetaphone("Washington"));
        assertEquals("", encoder.doubleMetaphone("Washington", true));
    }

    @Test
    public void testProtectedCharacterAndContainsHelpersHandleBoundaries() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals('B', encoder.charAt("ABC", 1));
        assertEquals(Character.MIN_VALUE, encoder.charAt("ABC", -1));
        assertEquals(Character.MIN_VALUE, encoder.charAt("ABC", 3));

        assertTrue(DoubleMetaphone.contains("ANGIER", 0, 3,
                new String[] { "ANG", "XYZ" }));
        assertFalse(DoubleMetaphone.contains("ANGIER", 0, 3,
                new String[] { "XYZ", "ABC" }));
        assertFalse(DoubleMetaphone.contains("ANGIER", -1, 3,
                new String[] { "ANG" }));
        assertFalse(DoubleMetaphone.contains("ANGIER", 4, 3,
                new String[] { "IER" }));
    }

    @Test
    public void testResultStoresPrimaryAndAlternateIndependentlyAndTruncates() {
        DoubleMetaphone encoder = new DoubleMetaphone();
        DoubleMetaphone.DoubleMetaphoneResult result =
                encoder.new DoubleMetaphoneResult(3);

        result.append('A');
        result.append('B', 'C');
        assertFalse(result.isComplete());

        result.appendPrimary("DE");
        result.appendAlternate("FG");

        assertEquals("ABD", result.getPrimary());
        assertEquals("ACF", result.getAlternate());
        assertTrue(result.isComplete());

        result.append('Z');
        assertEquals("ABD", result.getPrimary());
        assertEquals("ACF", result.getAlternate());
    }

    @Test
    public void testResultDirectPrimaryAlternateAndStringOverloadsRespectMaximum() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        DoubleMetaphone.DoubleMetaphoneResult paired =
                encoder.new DoubleMetaphoneResult(2);
        paired.append("AB", "CD");

        assertEquals("AB", paired.getPrimary());
        assertEquals("CD", paired.getAlternate());
        assertTrue(paired.isComplete());

        DoubleMetaphone.DoubleMetaphoneResult independentlyAppended =
                encoder.new DoubleMetaphoneResult(2);
        independentlyAppended.appendPrimary('A');
        independentlyAppended.appendAlternate('B');
        independentlyAppended.appendPrimary("CD");
        independentlyAppended.appendAlternate("EF");

        assertEquals("AC", independentlyAppended.getPrimary());
        assertEquals("BE", independentlyAppended.getAlternate());
        assertTrue(independentlyAppended.isComplete());
    }

    @Test
    public void testRepresentativeConsonantAndSpecialCharacterRules() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("PP", encoder.doubleMetaphone("bobby"));
        assertEquals("FST", encoder.doubleMetaphone("fa\u00e7ade"));
        assertEquals("NN", encoder.doubleMetaphone("ni\u00f1o"));
        assertEquals("AJ", encoder.doubleMetaphone("edge"));
        assertEquals("FLPS", encoder.doubleMetaphone("phelps"));
        assertEquals("KN", encoder.doubleMetaphone("queen"));
        assertEquals("FFN", encoder.doubleMetaphone("vivian"));
        assertEquals("PS", encoder.doubleMetaphone("bazz"));
        assertEquals("AH", encoder.doubleMetaphone("aha"));
    }

    @Test
    public void testRepresentativeCScTwxzAndJRulesIncludingAlternates() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("MK", encoder.doubleMetaphone("mccoy"));
        assertEquals("XSMT", encoder.doubleMetaphone("schmidt"));
        assertEquals("SSMT", encoder.doubleMetaphone("schmidt", true));
        assertEquals("TMS", encoder.doubleMetaphone("thomas"));
        assertEquals("AR", encoder.doubleMetaphone("awry"));
        assertEquals("SFR", encoder.doubleMetaphone("xavier"));
        assertEquals("J", encoder.doubleMetaphone("zhao"));
        assertEquals("JNS", encoder.doubleMetaphone("jones"));
        assertEquals("ANS", encoder.doubleMetaphone("jones", true));
    }

    @Test
    public void testRepresentativeGAndCAlternateRules() {
        DoubleMetaphone encoder = new DoubleMetaphone();

        assertEquals("KRS", encoder.doubleMetaphone("garcia"));
        assertEquals("KRX", encoder.doubleMetaphone("garcia", true));
        assertEquals("LF", encoder.doubleMetaphone("laugh"));
        assertEquals("TMR", encoder.doubleMetaphone("dumber"));
        assertEquals("MKL", encoder.doubleMetaphone("michael"));
        assertEquals("MXL", encoder.doubleMetaphone("michael", true));
    }
}
