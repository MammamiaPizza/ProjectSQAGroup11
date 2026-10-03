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

@org.junit.Test
public void caverphoneEqualityUsesGeneratedCodesForNullAndEmptyValues() {
    org.apache.commons.codec.language.Caverphone caverphone =
            new org.apache.commons.codec.language.Caverphone();

    org.junit.Assert.assertTrue(caverphone.isCaverphoneEqual(null, ""));
    org.junit.Assert.assertFalse(caverphone.isCaverphoneEqual(null, "I"));
}

@org.junit.Test(expected = org.apache.commons.codec.EncoderException.class)
public void metaphoneObjectEncodingRejectsNonStringValues() throws org.apache.commons.codec.EncoderException {
    new org.apache.commons.codec.language.Metaphone().encode((Object) new java.lang.Object());
}

@org.junit.Test
public void metaphoneEqualityHandlesEquivalentEmptyCodes() {
    org.apache.commons.codec.language.Metaphone metaphone =
            new org.apache.commons.codec.language.Metaphone();

    org.junit.Assert.assertTrue(metaphone.isMetaphoneEqual(null, ""));
    org.junit.Assert.assertFalse(metaphone.isMetaphoneEqual(null, "i"));
}

@org.junit.Test
public void soundexUtilsHandlesNullAndCountsMatchingEncodedCharacters()
        throws org.apache.commons.codec.EncoderException {
    org.junit.Assert.assertNull(org.apache.commons.codec.language.SoundexUtils.clean(null));
    org.junit.Assert.assertEquals(2,
            org.apache.commons.codec.language.SoundexUtils.differenceEncoded("ABCD", "ABXY"));

    org.apache.commons.codec.language.Metaphone metaphone =
            new org.apache.commons.codec.language.Metaphone();
    org.junit.Assert.assertEquals(metaphone.encode("Smith").length(),
            org.apache.commons.codec.language.SoundexUtils.difference(metaphone, "Smith", "Smith"));
}
}
