package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomStringUtilsLang805Test {

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void randomWithCharacterArrayRejectsEndBeyondArrayLength() {
        RandomStringUtils.random(1, 2, 3, false, false, new char[] { 'a', 'b' });
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void randomWithCharacterArrayRejectsNegativeStart() {
        RandomStringUtils.random(1, -1, 0, false, false, new char[] { 'a' });
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomWithEmptyCharacterArrayRejectsPositiveCount() {
        RandomStringUtils.random(1, new char[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomWithEmptyStringCharacterSetRejectsPositiveCount() {
        RandomStringUtils.random(1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomRejectsNegativeCountWhenUsingCharacterArrayOverload() {
        RandomStringUtils.random(-1, new char[] { 'a' });
    }

    @Test
    public void randomWithZeroCountReturnsEmptyStringEvenForCharacterArrayOverload() {
        assertEquals("", RandomStringUtils.random(0, new char[] { 'a', 'b' }));
    }

    @Test
    public void randomUsesOnlyCharactersInSpecifiedValidSubrange() {
        final char[] characters = { 'a', 'b', 'c', 'd' };

        final String result = RandomStringUtils.random(100, 1, 3, false, false, characters);

        assertEquals(100, result.length());
        for (int i = 0; i < result.length(); i++) {
            assertTrue(result.charAt(i) == 'b' || result.charAt(i) == 'c');
        }
    }

    @Test
    public void randomWithZeroStartAndEndUsesSuppliedCharacterArray() {
        final String result = RandomStringUtils.random(50, 0, 0, false, false,
                new char[] { 'x', 'y' });

        assertEquals(50, result.length());
        for (int i = 0; i < result.length(); i++) {
            assertTrue(result.charAt(i) == 'x' || result.charAt(i) == 'y');
        }
    }

    @Test
    public void randomCharacterArrayOverloadProducesRequestedLengthAndAllowedCharacters() {
        final String result = RandomStringUtils.random(25, new char[] { 'q' });

        assertEquals(25, result.length());
        assertEquals("qqqqqqqqqqqqqqqqqqqqqqqqq", result);
    }

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
}
