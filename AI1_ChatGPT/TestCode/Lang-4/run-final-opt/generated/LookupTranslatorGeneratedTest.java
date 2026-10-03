package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class LookupTranslatorGeneratedTest {

    @Test
    public void testThreeCharacterStringBuilderKeyIsMatchedAndConsumed() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuilder("abc"), "translated" });
        final StringWriter writer = new StringWriter();

        final int consumed = translator.translate("abc", 0, writer);

        assertEquals(3, consumed);
        assertEquals("translated", writer.toString());
    }

    @Test
    public void testNonStringKeyMatchesAtNonzeroIndex() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuffer("token"), "VALUE" });
        final StringWriter writer = new StringWriter();

        final int consumed = translator.translate("xx token yy", 3, writer);

        assertEquals(5, consumed);
        assertEquals("VALUE", writer.toString());
    }

    @Test
    public void testLongestMatchingKeyIsPreferred() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "a", "one" },
                new CharSequence[] { "abc", "three" },
                new CharSequence[] { "abcd", "four" });
        final StringWriter writer = new StringWriter();

        final int consumed = translator.translate("abcdx", 0, writer);

        assertEquals(4, consumed);
        assertEquals("four", writer.toString());
    }

    @Test
    public void testNoMatchConsumesNothingAndWritesNothing() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "cat", "feline" },
                new CharSequence[] { "dog", "canine" });
        final StringWriter writer = new StringWriter();

        final int consumed = translator.translate("bird", 0, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testShortestAndLongestKeysMatchAtAvailableBoundaries() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuilder("x"), "short" },
                new CharSequence[] { new StringBuilder("wxyz"), "long" });

        final StringWriter shortWriter = new StringWriter();
        final int shortConsumed = translator.translate("x", 0, shortWriter);

        assertEquals(1, shortConsumed);
        assertEquals("short", shortWriter.toString());

        final StringWriter longWriter = new StringWriter();
        final int longConsumed = translator.translate("wxyz", 0, longWriter);

        assertEquals(4, longConsumed);
        assertEquals("long", longWriter.toString());
    }
}
