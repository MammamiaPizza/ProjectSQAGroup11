package org.apache.commons.codec.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class SoundexCodec15Test {

    @Test
    public void soundexEmitsRepeatedCodeAfterVowelAndH() {
        assertEquals("Y330", new Soundex().soundex("YDAHT"));
    }

    @Test
    public void encodeEmitsRepeatedCodeAfterVowelAndW() {
        assertEquals("Y330", new Soundex().encode("YDAWT"));
    }

    @Test
    public void soundexTreatsSameCodeSeparatedByHAsOne() {
        assertEquals("Y300", new Soundex().soundex("YDHT"));
    }

    @Test
    public void soundexTreatsSameCodeSeparatedByWAsOne() {
        assertEquals("Y300", new Soundex().soundex("YDWT"));
    }

    @Test
    public void soundexEmitsRepeatedCodeWhenSeparatedByVowelWithoutHW() {
        assertEquals("Y330", new Soundex().soundex("YDAT"));
    }

    @Test
    public void soundexProducesStandardCodeForOrdinaryName() {
        assertEquals("R163", new Soundex().soundex("Robert"));
    }

    @Test
    public void soundexPadsSingleLetterCodeToFourCharacters() {
        assertEquals("A000", new Soundex().soundex("A"));
    }

    @Test
    public void soundexHandlesNullAndEmptyInput() {
        Soundex soundex = new Soundex();
        assertNull(soundex.soundex(null));
        assertEquals("", soundex.soundex(""));
    }

    @Test(expected = EncoderException.class)
    public void encodeObjectRejectsNonStringValues() throws EncoderException {
        new Soundex().encode(Integer.valueOf(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void soundexRejectsLettersOutsideTheConfiguredMapping() {
        new Soundex().soundex("A\u00C9");
    }

@Test
public void soundexEmitsSameCodeAfterVowelAndHForAnotherCodeGroup() {
    assertEquals("Y220", new Soundex().soundex("YCAHS"));
}

@Test
public void soundexConstructorsUseProvidedStandardMapping() {
    final char[] mapping = Soundex.US_ENGLISH_MAPPING_STRING.toCharArray();
    final Soundex arraySoundex = new Soundex(mapping);
    mapping['R' - 'A'] = '0';

    assertEquals("R163", arraySoundex.soundex("Robert"));
    assertEquals("R163", new Soundex(Soundex.US_ENGLISH_MAPPING_STRING).soundex("Robert"));
}

@Test
public void encodeObjectAcceptsStringValues() throws Exception {
    assertEquals("R163", new Soundex().encode((Object) "Robert"));
}

@Test
public void differenceAndMaximumLengthAccessorsWork() throws Exception {
    final Soundex soundex = new Soundex();
    soundex.setMaxLength(6);

    assertEquals(6, soundex.getMaxLength());
    assertEquals(4, soundex.difference("Robert", "Rupert"));
}
}
