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
    public void soundexDoesNotSuppressConsonantAfterHAndWWhenThePrecedingCharacterIsAlsoHOrW() {
        final Soundex soundex = new Soundex();

        assertEquals("Y330", soundex.soundex("YDHWD"));
    }

    @Test
    public void soundexDoesNotApplyHwSuppressionWhenWPrecedesTheSeparator() {
        final Soundex soundex = new Soundex();

        assertEquals("B330", soundex.soundex("BDWWD"));
    }

    @Test
    public void soundexStopsWhenTheFourthCodeIsProducedByTheFinalInputCharacter() {
        final Soundex soundex = new Soundex();

        assertEquals("B263", soundex.soundex("BCRD"));
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
    @SuppressWarnings("deprecation")
    public void deprecatedMaxLengthPropertyCanBeReadAndWrittenButDoesNotChangeCodeLength() {
        final Soundex soundex = new Soundex();

        assertEquals(4, soundex.getMaxLength());

        soundex.setMaxLength(1);

        assertEquals(1, soundex.getMaxLength());
        assertEquals("R163", soundex.soundex("Robert"));
    }
}
