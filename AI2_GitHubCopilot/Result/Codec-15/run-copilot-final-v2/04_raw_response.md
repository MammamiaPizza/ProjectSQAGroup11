@Test
public void testSoundexCharMappingConstructor() {
    char[] customMap = new char[26];
    for (int i = 0; i < customMap.length; i++) {
        customMap[i] = '0';
    }
    Soundex soundex = new Soundex(customMap);
    assertEquals("A000", soundex.soundex("ABC"));
}

@Test
public void testSoundexStringMappingConstructor() {
    String customMapStr = "00000000000000000000000000";
    Soundex soundex = new Soundex(customMapStr);
    assertEquals("A000", soundex.soundex("ABC"));
}

@Test
public void testEncodeObjectWithString() throws org.apache.commons.codec.EncoderException {
    Soundex soundex = new Soundex();
    assertEquals("A000", soundex.encode((Object) "A"));
}

@Test(expected = IllegalArgumentException.class)
public void testSoundexThrowsExceptionForInvalidCharacter() {
    new Soundex().soundex("a");
}