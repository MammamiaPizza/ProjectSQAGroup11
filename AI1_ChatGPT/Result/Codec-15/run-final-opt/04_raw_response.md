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