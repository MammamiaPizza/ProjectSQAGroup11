package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class Codec1LocaleIndependenceTest {

    @Test
    public void caverphoneUsesLocaleIndependentLowerCaseForStringAndObjectEncoding()
            throws EncoderException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            Caverphone caverphone = new Caverphone();

            assertEquals("A111111111", caverphone.caverphone("I"));
            assertEquals("A111111111", caverphone.encode("I"));
            assertEquals("A111111111", (String) caverphone.encode((Object) "I"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void caverphoneReturnsPaddingForNullAndEmptyInput() {
        Caverphone caverphone = new Caverphone();

        assertEquals("1111111111", caverphone.caverphone(null));
        assertEquals("1111111111", caverphone.caverphone(""));
    }

    @Test(expected = EncoderException.class)
    public void caverphoneObjectEncodingRejectsNonStringValues() throws EncoderException {
        new Caverphone().encode((Object) Integer.valueOf(1));
    }

    @Test
    public void metaphoneUsesLocaleIndependentUpperCaseForSingleCharacterStringAndObjectEncoding()
            throws EncoderException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            Metaphone metaphone = new Metaphone();

            assertEquals("I", metaphone.metaphone("i"));
            assertEquals("I", metaphone.encode("i"));
            assertEquals("I", (String) metaphone.encode((Object) "i"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void metaphoneReturnsEmptyCodeForNullAndEmptyInput() {
        Metaphone metaphone = new Metaphone();

        assertEquals("", metaphone.metaphone(null));
        assertEquals("", metaphone.metaphone(""));
    }

    @Test
    public void soundexUtilsCleanUsesEnglishUpperCaseForAllLetterInput() {
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
    public void soundexEncodingIsIndependentOfTurkishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Soundex soundex = new Soundex();

            Locale.setDefault(Locale.ENGLISH);
            String englishCode = soundex.soundex("i");

            Locale.setDefault(new Locale("tr", "TR"));
            assertEquals(englishCode, soundex.soundex("i"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void refinedSoundexEncodingIsIndependentOfTurkishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            RefinedSoundex soundex = new RefinedSoundex();

            Locale.setDefault(Locale.ENGLISH);
            String englishCode = soundex.soundex("i");

            Locale.setDefault(new Locale("tr", "TR"));
            assertEquals(englishCode, soundex.soundex("i"));
        } finally {
            Locale.setDefault(original);
        }
    }
}
