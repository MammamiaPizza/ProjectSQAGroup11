package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class LookupTranslatorLang882Test {

    @Test
    public void testMutableCharSequenceKeyConsumesThreeCharacters() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuilder("abc"), "translated" });
        final StringWriter out = new StringWriter();

        assertEquals(3, translator.translate("abc", 0, out));
        assertEquals("translated", out.toString());
    }

    @Test
    public void testMutableCharSequenceKeyMatchesAtNonZeroIndex() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuilder("cat"), "dog" });
        final StringWriter out = new StringWriter();

        assertEquals(3, translator.translate("xxcatyy", 2, out));
        assertEquals("dog", out.toString());
    }

    @Test
    public void testGreedyLongestMatchIsUsed() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "a", "one" },
                new CharSequence[] { "ab", "two" },
                new CharSequence[] { "abc", "three" });
        final StringWriter out = new StringWriter();

        assertEquals(3, translator.translate("abcd", 0, out));
        assertEquals("three", out.toString());
    }

    @Test
    public void testShortestConfiguredKeyCanMatchAtInputBoundary() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "x", "short" },
                new CharSequence[] { "xyz", "long" });
        final StringWriter out = new StringWriter();

        assertEquals(1, translator.translate("x", 0, out));
        assertEquals("short", out.toString());
    }

    @Test
    public void testAvailablePrefixMatchesWhenLongestKeyDoesNotFit() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "ab", "prefix" },
                new CharSequence[] { "abcd", "full" });
        final StringWriter out = new StringWriter();

        assertEquals(2, translator.translate("ab", 0, out));
        assertEquals("prefix", out.toString());
    }

    @Test
    public void testNoMatchReturnsZeroAndDoesNotWrite() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { "abc", "replacement" });
        final StringWriter out = new StringWriter();
        out.write("existing");

        assertEquals(0, translator.translate("abd", 0, out));
        assertEquals("existing", out.toString());
    }

    @Test
    public void testCharSequenceValueIsWrittenAsText() throws Exception {
        final LookupTranslator translator = new LookupTranslator(
                new CharSequence[] { new StringBuffer("key"), new StringBuilder("value") });
        final StringWriter out = new StringWriter();

        assertEquals(3, translator.translate("key", 0, out));
        assertEquals("value", out.toString());
    }
}
