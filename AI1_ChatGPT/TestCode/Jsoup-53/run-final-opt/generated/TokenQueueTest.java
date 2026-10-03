package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenQueueTest {

    @Test
    public void chompBalancedKeepsClosingBracketInsideSingleQuotedValue() {
        TokenQueue queue = new TokenQueue("[data='End]'] trailing");

        assertEquals("data='End]'", queue.chompBalanced('[', ']'));
        assertEquals(" trailing", queue.remainder());
    }

    @Test
    public void chompBalancedKeepsClosingBracketInsideDoubleQuotedValue() {
        TokenQueue queue = new TokenQueue("[data=\"End]\"] trailing");

        assertEquals("data=\"End]\"", queue.chompBalanced('[', ']'));
        assertEquals(" trailing", queue.remainder());
    }

    @Test
    public void chompBalancedHandlesNestedDelimiters() {
        TokenQueue queue = new TokenQueue("[outer[inner]tail]after");

        assertEquals("outer[inner]tail", queue.chompBalanced('[', ']'));
        assertEquals("after", queue.remainder());
    }

    @Test
    public void chompBalancedHandlesUnquotedAttributeContent() {
        TokenQueue queue = new TokenQueue("[data=End]after");

        assertEquals("data=End", queue.chompBalanced('[', ']'));
        assertEquals("after", queue.remainder());
    }

    @Test
    public void chompBalancedPreservesEscapedQuoteAndBracketInsideQuotedValue() {
        TokenQueue queue = new TokenQueue("[data='it\\'s ] still']next");

        assertEquals("data='it\\'s ] still'", queue.chompBalanced('[', ']'));
        assertEquals("next", queue.remainder());
    }

    @Test
    public void chompBalancedPreservesEscapedClosingDelimiter() {
        TokenQueue queue = new TokenQueue("[data=one\\]two]next");

        assertEquals("data=one\\]two", queue.chompBalanced('[', ']'));
        assertEquals("next", queue.remainder());
    }

    @Test
    public void chompBalancedReturnsAvailableContentWhenClosingDelimiterIsMissing() {
        TokenQueue queue = new TokenQueue("[data=value");

        assertEquals("data=value", queue.chompBalanced('[', ']'));
        assertTrue(queue.isEmpty());
    }

    @Test
    public void chompBalancedReturnsEmptyStringForEmptyInput() {
        TokenQueue queue = new TokenQueue("");

        assertEquals("", queue.chompBalanced('[', ']'));
        assertTrue(queue.isEmpty());
    }

    @Test
    public void selectorMatchesSingleQuotedAttributeValueContainingClosingBracket() {
        Document document = Jsoup.parse("<div data='End]'></div><div data='other'></div>");

        assertEquals(1, document.select("div[data='End]']").size());
    }

    @Test
    public void selectorMatchesDoubleQuotedAttributeValueContainingClosingBracket() {
        Document document = Jsoup.parse("<div data=\"End]\"></div><div data=\"other\"></div>");

        assertEquals(1, document.select("div[data=\"End]\"]").size());
    }
}
