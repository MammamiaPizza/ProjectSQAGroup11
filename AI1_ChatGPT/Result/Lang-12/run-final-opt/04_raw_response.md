@org.junit.Test
public void randomDefaultOverloadProducesRequestedLength() {
    final String result = RandomStringUtils.random(32);

    org.junit.Assert.assertEquals(32, result.length());
}

@org.junit.Test
public void randomConvenienceMethodsRespectTheirCharacterCategories() {
    final String ascii = RandomStringUtils.randomAscii(32);
    for (int i = 0; i < ascii.length(); i++) {
        org.junit.Assert.assertTrue(ascii.charAt(i) >= ' ' && ascii.charAt(i) < 127);
    }

    final String alphabetic = RandomStringUtils.randomAlphabetic(32);
    for (int i = 0; i < alphabetic.length(); i++) {
        org.junit.Assert.assertTrue(Character.isLetter(alphabetic.charAt(i)));
    }

    final String alphanumeric = RandomStringUtils.randomAlphanumeric(32);
    for (int i = 0; i < alphanumeric.length(); i++) {
        org.junit.Assert.assertTrue(Character.isLetterOrDigit(alphanumeric.charAt(i)));
    }

    final String numeric = RandomStringUtils.randomNumeric(32);
    for (int i = 0; i < numeric.length(); i++) {
        org.junit.Assert.assertTrue(Character.isDigit(numeric.charAt(i)));
    }
}

@org.junit.Test
public void randomWithExplicitRangeAndNumericFilterUsesOnlyDigitsInRange() {
    final String result = RandomStringUtils.random(32, '0', '3', false, true);

    org.junit.Assert.assertEquals(32, result.length());
    for (int i = 0; i < result.length(); i++) {
        org.junit.Assert.assertTrue(result.charAt(i) >= '0' && result.charAt(i) < '3');
    }
}

@org.junit.Test
public void randomCharacterArrayOverloadCreatesValidSurrogatePairs() {
    final String fromHighSurrogate = RandomStringUtils.random(2, new char[] { '\uD800' });
    org.junit.Assert.assertEquals('\uD800', fromHighSurrogate.charAt(0));
    org.junit.Assert.assertTrue(Character.isLowSurrogate(fromHighSurrogate.charAt(1)));

    final String fromLowSurrogate = RandomStringUtils.random(2, new char[] { '\uDC00' });
    org.junit.Assert.assertTrue(Character.isHighSurrogate(fromLowSurrogate.charAt(0)));
    org.junit.Assert.assertEquals('\uDC00', fromLowSurrogate.charAt(1));
}