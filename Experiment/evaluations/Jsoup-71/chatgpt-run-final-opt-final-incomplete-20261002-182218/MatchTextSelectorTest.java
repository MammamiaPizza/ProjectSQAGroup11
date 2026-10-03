package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.PseudoTextElement;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MatchTextSelectorTest {

    @Test
    public void matchTextSelectsEachDirectTextSegmentSeparatedByBreaks() {
        Document document = Jsoup.parse("<p>One<br>Two<br>Three</p>");
        Element paragraph = document.selectFirst("p");

        Elements matches = document.select("p:matchText");

        assertEquals(3, matches.size());
        assertEquals("One", matches.get(0).text());
        assertEquals("Two", matches.get(1).text());
        assertEquals("Three", matches.get(2).text());
        assertTrue(matches.get(0) instanceof PseudoTextElement);
        assertTrue(matches.get(1) instanceof PseudoTextElement);
        assertTrue(matches.get(2) instanceof PseudoTextElement);
        assertSame(paragraph, matches.get(0).parent());
        assertSame(paragraph, matches.get(1).parent());
        assertSame(paragraph, matches.get(2).parent());
    }

    @Test
    public void matchTextCanBeChainedWithFirstChild() {
        Document document = Jsoup.parse("<p>One<br>Two<br>Three</p>");
        Element paragraph = document.selectFirst("p");

        Elements matches = document.select("p:matchText:first-child");

        assertEquals(1, matches.size());
        assertEquals("One", matches.first().text());
        assertTrue(matches.first() instanceof PseudoTextElement);
        assertSame(paragraph, matches.first().parent());
        assertEquals(0, matches.first().elementSiblingIndex());
    }

    @Test
    public void matchTextDoesNotMatchElementsWithoutDirectTextNodes() {
        Document document = Jsoup.parse("<p><br></p>");

        Elements matches = document.select("p:matchText");

        assertEquals(0, matches.size());
    }

@Test
public void matchTextSelectsOnlyDirectTextWhenInlineElementsArePresent() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<span>Two</span>Three</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText");

    org.junit.Assert.assertEquals(2, matches.size());
    org.junit.Assert.assertEquals("One", matches.get(0).text());
    org.junit.Assert.assertEquals("Three", matches.get(1).text());
    org.junit.Assert.assertSame(document.selectFirst("p"), matches.get(0).parent());
    org.junit.Assert.assertSame(document.selectFirst("p"), matches.get(1).parent());
}

@Test
public void matchTextCanMatchInheritedParentClass() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p class='note'>One<br>Two</p><p>Three</p>");

    org.jsoup.select.Elements matches = document.select("p.note:matchText");

    org.junit.Assert.assertEquals(2, matches.size());
    org.junit.Assert.assertEquals("One", matches.get(0).text());
    org.junit.Assert.assertEquals("Two", matches.get(1).text());
    org.junit.Assert.assertEquals("note", matches.get(0).className());
    org.junit.Assert.assertEquals("note", matches.get(1).className());
}

@Test
public void matchTextCanBeChainedWithLastChild() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<br>Two<br>Three</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText:last-child");

    org.junit.Assert.assertEquals(1, matches.size());
    org.junit.Assert.assertEquals("Three", matches.first().text());
}

@Test
public void matchTextPseudoElementsSerializeAsTheirTextOnly() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<br>Two</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText");

    org.junit.Assert.assertEquals("One", matches.get(0).outerHtml());
    org.junit.Assert.assertEquals("Two", matches.get(1).outerHtml());
    org.junit.Assert.assertEquals("<p>One<br>Two</p>", document.selectFirst("p").outerHtml());
}
}
