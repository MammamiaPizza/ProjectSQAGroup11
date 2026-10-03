package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.TokenQueue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QueryParserRegressionTest {

    @Test(expected = Selector.SelectorParseException.class)
    public void rejectsContainsWithUnclosedSingleQuote() {
        QueryParser.parse("a:contains(')");
    }

    @Test(expected = Selector.SelectorParseException.class)
    public void rejectsUnclosedAttributeSelector() {
        QueryParser.parse("[href");
    }

    @Test(expected = Selector.SelectorParseException.class)
    public void rejectsContainsWithUnclosedDoubleQuote() {
        QueryParser.parse("a:contains(\"unterminated)");
    }

    @Test(expected = Selector.SelectorParseException.class)
    public void rejectsContainsWithUnclosedParenthesis() {
        QueryParser.parse("a:contains(unterminated");
    }

    @Test
    public void chompBalancedPreservesNestedContentAndQuotedClosingMarker() {
        TokenQueue queue = new TokenQueue("(one (two) \"three)\" four)tail");

        assertEquals("one (two) \"three)\" four", queue.chompBalanced('(', ')'));
        assertEquals("tail", queue.remainder());
    }

    @Test
    public void parsesAndExecutesValidAttributeAndContainsSelector() {
        Document document = Jsoup.parse(
                "<div data-state='active'>Hello world</div><div data-state='inactive'>Other</div>");

        assertEquals(1, document.select("div[data-state=active]:contains(Hello)").size());
        assertEquals("Hello world",
                document.select("div[data-state=active]:contains(Hello)").first().text());
    }

    @Test
    public void parsesValidSingleQuotedBalancedContainsArgument() {
        assertNotNull(QueryParser.parse("a:contains('hello')"));
    }
}
