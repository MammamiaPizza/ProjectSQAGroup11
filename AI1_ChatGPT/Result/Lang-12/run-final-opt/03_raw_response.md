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
}