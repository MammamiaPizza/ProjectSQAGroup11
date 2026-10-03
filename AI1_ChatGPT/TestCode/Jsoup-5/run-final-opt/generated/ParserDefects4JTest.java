package org.jsoup.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserDefects4JTest {

    @Test
    public void parsesQuotedAndUnquotedAttributes() {
        Document document = Parser.parse(
                "<div id='one' data-value=two title=\"three four\">Text</div>",
                "http://example.com/");

        Element div = document.select("div").get(0);
        assertEquals("one", div.attr("id"));
        assertEquals("two", div.attr("data-value"));
        assertEquals("three four", div.attr("title"));
        assertEquals("Text", div.text());
    }

    @Test
    public void parsesBodyFragmentAttributes() {
        Document document = Parser.parseBodyFragment(
                "<input checked disabled value=test>",
                "http://example.com/");

        Element input = document.select("input").get(0);
        assertEquals("", input.attr("checked"));
        assertEquals("", input.attr("disabled"));
        assertEquals("test", input.attr("value"));
    }

    @Test
    public void parsesRoughAttributeBeforeNormalAttributes() {
        Document document = Parser.parse("<p =howdy id=1>One</p>", "");

        Element paragraph = document.select("p").get(0);
        assertEquals("1", paragraph.attr("id"));
        assertEquals("One", paragraph.text());
    }

    @Test
    public void handlesTrailingWhitespaceAfterAttributeAtEndOfInput() {
        Document document = Parser.parse("<p id=1 class ", "");

        Element paragraph = document.select("p").get(0);
        assertEquals("1", paragraph.attr("id"));
        assertEquals("", paragraph.attr("class"));
    }

    @Test
    public void handlesTrailingWhitespaceAfterAttributeInBodyFragment() {
        Document document = Parser.parseBodyFragment("<p id=1 class ", "");

        Element paragraph = document.select("p").get(0);
        assertEquals("1", paragraph.attr("id"));
        assertEquals("", paragraph.attr("class"));
    }

    @Test
    public void parsesUnquotedAttributeBeforeSelfClosingDelimiter() {
        Document document = Parser.parse("<div data-value=abc/>", "");

        Element div = document.select("div").get(0);
        assertEquals("abc", div.attr("data-value"));
    }
}
