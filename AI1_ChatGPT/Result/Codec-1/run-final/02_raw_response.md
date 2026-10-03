```java
package org.apache.commons.codec.language;

import java.util.Locale;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LocaleIndependentLanguageEncodersTest {

    @Test
    public void caverphoneHandlesNullEmptyAndNonAlphabeticInput() {
        Caverphone encoder = new Caverphone();

        assertEquals("1111111111", encoder.caverphone(null));
        assertEquals("1111111111", encoder.caverphone(""));
        assertEquals("1111111111", encoder.caverphone("1234-!"));
    }

    @Test
    public void caverphoneProducesTenCharacterCodesAndIgnoresPunctuation() {
        Caverphone encoder = new Caverphone();

        String plain = encoder.caverphone("Smith");
        String punctuated = encoder.caverphone("S-m.i,t!h");

        assertEquals(10, plain.length());
        assertEquals(plain, punctuated);
        assertTrue(encoder.isCaverphoneEqual("Smith", "Smith"));
        assertFalse(encoder.isCaverphoneEqual(null, "A"));
    }

    @Test
    public void caverphoneEncodeOverloadsDelegateAndRejectNonStrings() throws Exception {
        Caverphone encoder = new Caverphone();

        assertEquals(encoder.caverphone("Peter"), encoder.encode("Peter"));
        assertEquals(encoder.caverphone("Peter"), encoder.encode((Object) "Peter"));
        assertEquals("1111111111", encoder.encode((String) null));

        try {
            encoder.encode((Object) Integer.valueOf(1));
            fail("Encoding a non-String Object must throw EncoderException");
        } catch (EncoderException expected) {
            assertTrue(expected.getMessage().contains("not of type java.lang.String"));
        }
    }

    @Test
    public void caverphoneUsesEnglishLowerCasingRegardlessOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));

            assertEquals("A111111111", new Caverphone().caverphone("I"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void metaphoneHandlesNullEmptySingleCharacterAndInitialRules() {
        Metaphone encoder = new Metaphone();

        assertEquals("", encoder.metaphone(null));
        assertEquals("", encoder.metaphone(""));
        assertEquals("A", encoder.metaphone("a"));

        assertEquals("NT", encoder.metaphone("knight"));
        assertEquals("E", encoder.metaphone("ae"));
        assertEquals("W", encoder.metaphone("wh"));
        assertEquals("SNN", encoder.metaphone("xenon"));
    }

    @Test
    public void metaphoneCoversRepresentativeConsonantTransformations() {
        Metaphone encoder = new Metaphone();

        assertEquals("X", encoder.metaphone("cia"));
        assertEquals("J", encoder.metaphone("dge"));
        assertEquals("FS", encoder.metaphone("phase"));
        assertEquals("0MS", encoder.metaphone("thomas"));
    }

    @Test
    public void metaphoneHonorsConfiguredMaximumCodeLength() {
        Metaphone encoder = new Metaphone();

        assertEquals(4, encoder.getMaxCodeLen());

        encoder.setMaxCodeLen(2);
        assertEquals(2, encoder.getMaxCodeLen());
        assertEquals("0M", encoder.metaphone("thomas"));

        encoder.setMaxCodeLen(0);
        assertEquals("", encoder.metaphone("thomas"));
    }

    @Test
    public void metaphoneEncodeOverloadsEqualityAndInvalidObjectHandling() throws Exception {
        Metaphone encoder = new Metaphone();

        assertEquals(encoder.metaphone("Smith"), encoder.encode("Smith"));
        assertEquals(encoder.metaphone("Smith"), encoder.encode((Object) "Smith"));
        assertEquals("", encoder.encode((String) null));
        assertTrue(encoder.isMetaphoneEqual("Smith", "Smith"));
        assertFalse(encoder.isMetaphoneEqual(null, "Smith"));

        try {
            encoder.encode((Object) new Object());
            fail("Encoding a non-String Object must throw EncoderException");
        } catch (EncoderException expected) {
            assertTrue(expected.getMessage().contains("not of type java.lang.String"));
        }
    }

    @Test
    public void metaphoneUsesEnglishUpperCasingRegardlessOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));

            assertEquals("I", new Metaphone().metaphone("i"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void soundexUtilsCleanHandlesNullEmptyLettersAndNonLetters() {
        assertSame(null, SoundexUtils.clean(null));
        assertEquals("", SoundexUtils.clean(""));
        assertEquals("ABC", SoundexUtils.clean("aBc"));
        assertEquals("AB", SoundexUtils.clean("A1-b!"));
    }

    @Test
    public void soundexUtilsCleanUsesEnglishUpperCasingRegardlessOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));

            assertEquals("I", SoundexUtils.clean("i"));
            assertEquals("I", SoundexUtils.clean("i!"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void soundexUtilsDifferenceEncodedCountsOnlyMatchingPositions() {
        assertEquals(0, SoundexUtils.differenceEncoded(null, "A123"));
        assertEquals(0, SoundexUtils.differenceEncoded("A123", null));
        assertEquals(3, SoundexUtils.differenceEncoded("A123", "A153"));
        assertEquals(2, SoundexUtils.differenceEncoded("AB", "ABC"));
        assertEquals(0, SoundexUtils.differenceEncoded("", ""));
    }

    @Test
    public void soundexUtilsDifferenceUsesEncoderResultsAndPropagatesEncoderExceptions() {
        StringEncoder encoder = new StringEncoder() {
            public String encode(String source) {
                return "left".equals(source) ? "A12" : "A13";
            }

            public Object encode(Object source) throws EncoderException {
                if (!(source instanceof String)) {
                    throw new EncoderException("String required");
                }
                return encode((String) source);
            }
        };

        assertEquals(2, SoundexUtils.difference(encoder, "left", "right"));

        StringEncoder failingEncoder = new StringEncoder() {
            public String encode(String source) throws EncoderException {
                throw new EncoderException("encoding failed");
            }

            public Object encode(Object source) throws EncoderException {
                return encode((String) source);
            }
        };

        try {
            SoundexUtils.difference(failingEncoder, "first", "second");
            fail("An EncoderException from the supplied encoder must propagate");
        } catch (EncoderException expected) {
            assertEquals("encoding failed", expected.getMessage());
        }
    }
}
```

Test coverage targets:

- **Caverphone null, empty, and sanitized input**: verifies documented fallback code and removal of non-letter characters.
- **Caverphone encoding API**: covers both `encode` overloads, equality behavior, and invalid-object `EncoderException`.
- **Caverphone locale independence**: executes with Turkish default locale and verifies that `"I"` remains an English vowel and encodes as `A111111111`.
- **Metaphone basic and initial-letter rules**: covers null/empty/single-character handling plus `KN`, `AE`, `WH`, and initial `X` handling.
- **Metaphone transformation branches**: covers `CIA`, `DGE`, `PH`, and `TH` transformations.
- **Metaphone maximum code length**: covers getter/setter behavior, truncation, and zero-length output.
- **Metaphone API and exceptions**: covers both encoder overloads, equality, null String behavior, and rejection of non-String objects.
- **Metaphone locale independence**: verifies Turkish locale does not convert `"i"` to Turkish capital dotted-I.
- **SoundexUtils.clean**: covers null, empty, all-letter, and filtered non-letter branches.
- **SoundexUtils locale independence**: verifies both all-letter and filtered paths use locale-independent English uppercasing.
- **SoundexUtils.differenceEncoded**: covers null inputs, unequal lengths, matching positions, and empty values.
- **SoundexUtils.difference**: verifies delegation to `StringEncoder` and propagation of `EncoderException`.