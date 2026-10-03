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