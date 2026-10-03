```java
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
}
```

- `testAlternateEncodingForAngierUsesJAsReportedByCodec84` directly targets CODEC-84: the alternate encoding of “Angier” must be `ANJR`, rather than the faulty `ANKR`.
- `testInputIsTrimmedCaseInsensitiveAndBlankOrNullProducesNull` covers input normalization, null handling, blank input handling, and the `encode(String)` overload.
- `testSilentStartProducesSameEncodingAsPronouncedWord` exercises silent-start handling for the `KN` prefix and equality behavior.
- `testEncodeObjectAcceptsStringsAndRejectsOtherObjects` covers valid object encoding plus exceptional paths for non-`String` and null `Object` inputs.
- `testEqualitySupportsPrimaryAndAlternateModes` covers both equality overloads, alternate mode, normalized equal values, and unequal encodings.
- `testMaximumCodeLengthLimitsBothEncodings` covers default and modified maximum code lengths, truncation of primary and alternate encodings, and the zero-length boundary.
- `testProtectedCharacterAndContainsHelpersHandleBoundaries` covers valid and out-of-range protected helper behavior.
- `testResultStoresPrimaryAndAlternateIndependentlyAndTruncates` covers the public inner result type, independent primary/alternate appends, string truncation, completion, and ignored appends after reaching capacity.