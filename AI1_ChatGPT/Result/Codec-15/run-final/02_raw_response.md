```java
package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class SoundexGeneratedTest {

    @Test
    public void soundexEncodesCanonicalNamesAndPadsToFourCharacters() {
        final Soundex soundex = new Soundex();

        assertEquals("R163", soundex.soundex("Robert"));
        assertEquals("R163", soundex.encode("Rupert"));
        assertEquals("A261", soundex.soundex("Ashcraft"));
    }

    @Test
    public void soundexSuppressesAdjacentConsonantsFromSameCodeGroup() {
        final Soundex soundex = new Soundex();

        assertEquals("B000", soundex.soundex("Bb"));
        assertEquals("B000", soundex.soundex("BHB"));
    }

    @Test
    public void soundexAllowsSameCodeAfterVowelResetsPreviousCode() {
        final Soundex soundex = new Soundex();

        assertEquals("B100", soundex.soundex("BAb"));
    }

    @Test
    public void soundexHandlesConsonantsSeparatedBySingleHOrWAccordingToHwRule() {
        final Soundex soundex = new Soundex();

        assertEquals("B000", soundex.soundex("BHB"));
        assertEquals("B000", soundex.soundex("BWB"));
        assertEquals("B200", soundex.soundex("BHC"));
    }

    @Test
    public void soundexDoesNotIncorrectlySuppressConsonantAfterConsecutiveHAndW() {
        final Soundex soundex = new Soundex();

        assertEquals("Y330", soundex.soundex("YDHWD"));
    }

    @Test
    public void soundexReturnsNullForNullAndEmptyForStringsWithoutLetters() {
        final Soundex soundex = new Soundex();

        assertNull(soundex.soundex(null));
        assertNull(soundex.encode((String) null));
        assertEquals("", soundex.soundex(""));
        assertEquals("", soundex.soundex("123! "));
    }

    @Test
    public void soundexCleansNonLetterCharactersBeforeEncoding() {
        final Soundex soundex = new Soundex();

        assertEquals("R163", soundex.soundex("  Robert-123  "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void soundexRejectsLettersOutsideTheConfiguredMapping() {
        new Soundex().soundex("\u00C9");
    }

    @Test
    public void objectEncodeAcceptsStrings() throws EncoderException {
        final Soundex soundex = new Soundex();

        assertEquals("R163", soundex.encode((Object) "Robert"));
    }

    @Test(expected = EncoderException.class)
    public void objectEncodeRejectsNonStringObjectsIncludingNull() throws EncoderException {
        new Soundex().encode((Object) null);
    }

    @Test
    public void differenceCountsMatchingPositionsOfEncodedValues() throws EncoderException {
        final Soundex soundex = new Soundex();

        assertEquals(4, soundex.difference("Robert", "Rupert"));
        assertEquals(2, soundex.difference("Robert", "Rubin"));
    }

    @Test
    public void charArrayConstructorCopiesItsMapping() {
        final char[] mapping = Soundex.US_ENGLISH_MAPPING_STRING.toCharArray();
        mapping['B' - 'A'] = '9';

        final Soundex soundex = new Soundex(mapping);
        mapping['B' - 'A'] = '8';

        assertEquals("A900", soundex.soundex("AB"));
    }

    @Test
    public void stringMappingConstructorUsesProvidedMapping() {
        final Soundex soundex = new Soundex(Soundex.US_ENGLISH_MAPPING_STRING);

        assertEquals("R163", soundex.soundex("Robert"));
    }

    @Test
    public void deprecatedMaxLengthPropertyCanBeReadAndWrittenButDoesNotChangeCodeLength() {
        final Soundex soundex = new Soundex();

        assertEquals(4, soundex.getMaxLength());

        soundex.setMaxLength(1);

        assertEquals(1, soundex.getMaxLength());
        assertEquals("R163", soundex.soundex("Robert"));
    }
}
```

Test coverage targets:

- `soundexEncodesCanonicalNamesAndPadsToFourCharacters`: normal Soundex encoding, standard mappings, and four-character output.
- `soundexSuppressesAdjacentConsonantsFromSameCodeGroup`: duplicate-code suppression for adjacent consonants.
- `soundexAllowsSameCodeAfterVowelResetsPreviousCode`: verifies that a vowel resets the previous mapping code.
- `soundexHandlesConsonantsSeparatedBySingleHOrWAccordingToHwRule`: exercises the normal H/W separator rule and a different-code case.
- `soundexDoesNotIncorrectlySuppressConsonantAfterConsecutiveHAndW`: targets CODEC-199. `YDHWD` must encode to `Y330`, distinguishing the fixed behavior from the reported faulty `Y300`.
- `soundexReturnsNullForNullAndEmptyForStringsWithoutLetters`: covers null, empty, and cleaned-empty input branches.
- `soundexCleansNonLetterCharactersBeforeEncoding`: verifies `SoundexUtils.clean` integration.
- `soundexRejectsLettersOutsideTheConfiguredMapping`: covers unmapped-character failure from `map`.
- `objectEncodeAcceptsStrings` and `objectEncodeRejectsNonStringObjectsIncludingNull`: cover both branches of `encode(Object)`.
- `differenceCountsMatchingPositionsOfEncodedValues`: covers `difference` delegation and matching-position counts.
- Constructor tests cover custom character-array mappings, defensive copying, and string mappings.
- `deprecatedMaxLengthPropertyCanBeReadAndWrittenButDoesNotChangeCodeLength`: covers deprecated accessors and confirms Soundex output remains four characters.