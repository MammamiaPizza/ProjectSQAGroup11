package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokeniserStateSlashAttributeTest {

    @Test
    public void retainsAttributeNameFollowingSlash() {
        Document document = Jsoup.parse("<div first/second=value></div>");
        Element element = document.select("div").first();

        assertTrue(element.hasAttr("first"));
        assertEquals("value", element.attr("second"));
    }

    @Test
    public void doesNotDropFirstCharacterOfAttributeNameFollowingSlash() {
        Document document = Jsoup.parse("<div alpha/beta></div>");
        Element element = document.select("div").first();

        assertTrue(element.hasAttr("alpha"));
        assertTrue(element.hasAttr("beta"));
        assertFalse(element.hasAttr("eta"));
    }

    @Test
    public void retainsMultipleAttributeNamesSeparatedBySlashes() {
        Document document = Jsoup.parse("<div one/two/three=3></div>");
        Element element = document.select("div").first();

        assertTrue(element.hasAttr("one"));
        assertTrue(element.hasAttr("two"));
        assertEquals("3", element.attr("three"));
    }

    @Test
    public void retainsAttributeAfterSlashBeforeWhitespace() {
        Document document = Jsoup.parse("<div first/ second=value></div>");
        Element element = document.select("div").first();

        assertTrue(element.hasAttr("first"));
        assertEquals("value", element.attr("second"));
    }

    @Test
    public void retainsAttributeBeforeSelfClosingDelimiter() {
        Document document = Jsoup.parse("<input enabled/>");
        Element element = document.select("input").first();

        assertTrue(element.hasAttr("enabled"));
        assertEquals("", element.attr("enabled"));
    }

    @Test
    public void retainsAttributeBeforeWhitespaceAfterSelfClosingDelimiter() {
        Document document = Jsoup.parse("<input enabled/ >");
        Element element = document.select("input").first();

        assertTrue(element.hasAttr("enabled"));
        assertEquals("", element.attr("enabled"));
    }
}