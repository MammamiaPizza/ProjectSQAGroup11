@org.junit.Test
public void testRandomDefaultProducesRequestedLength() {
    org.junit.Assert.assertEquals(32, org.apache.commons.lang3.RandomStringUtils.random(32).length());
}

@org.junit.Test
public void testRandomWithStringCharactersUsesOnlySuppliedCharacters() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(40, "pq");
    org.junit.Assert.assertEquals(40, value.length());
    for (int i = 0; i < value.length(); i++) {
        org.junit.Assert.assertTrue(value.charAt(i) == 'p' || value.charAt(i) == 'q');
    }
}

@org.junit.Test
public void testRandomLowSurrogateCharacterIsPairedWithHighSurrogate() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(
            2, 0, 0, false, false, new char[] { '\uDC00' });
    org.junit.Assert.assertEquals(2, value.length());
    org.junit.Assert.assertEquals('\uDC00', value.charAt(1));
    org.junit.Assert.assertTrue(java.lang.Character.isSurrogatePair(value.charAt(0), value.charAt(1)));
}

@org.junit.Test
public void testRandomHighSurrogateCharacterIsPairedWithLowSurrogate() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(
            2, 0, 0, false, false, new char[] { '\uD800' });
    org.junit.Assert.assertEquals(2, value.length());
    org.junit.Assert.assertEquals('\uD800', value.charAt(0));
    org.junit.Assert.assertTrue(java.lang.Character.isSurrogatePair(value.charAt(0), value.charAt(1)));
}