package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokeniserUnterminatedTextTest {
    @Test
    public void unterminatedTextareaStopsTextAtFollowingMarkup() {
        Document document = Jsoup.parse("<textarea>one<p>two");

        assertEquals("one", document.select("textarea").get(0).text());
    }

    @Test
    public void unterminatedTitleStopsTextAtFollowingMarkup() {
        Document document = Jsoup.parse("<title>One<b>Two <p>Test</p>");

        assertEquals("One", document.title());
    }

    @Test
    public void closedTextareaRetainsTextBeforeItsClosingTag() {
        Document document = Jsoup.parse("<textarea>one</textarea><p>two</p>");
        Element textarea = document.select("textarea").get(0);

        assertEquals("one", textarea.text());
    }

    @Test
    public void closedTitleRetainsTextBeforeItsClosingTag() {
        Document document = Jsoup.parse("<title>One</title><p>Two</p>");

        assertEquals("One", document.title());
    }

@Test
public void decodesNamedAndNumericCharacterReferencesInText() {
    org.junit.Assert.assertEquals("&<>\"AB",
        org.jsoup.Jsoup.parse("<p>&amp;&lt;&gt;&quot;&#65;&#x42;</p>").select("p").text());
}

@Test
public void decodesCharacterReferencesInAttributesWithoutConsumingLiteralAmpersands() {
    org.junit.Assert.assertEquals("a&b",
        org.jsoup.Jsoup.parse("<a title='a&amp;b' href='?x=1&y=2'>link</a>").select("a").attr("title"));
    org.junit.Assert.assertEquals("?x=1&y=2",
        org.jsoup.Jsoup.parse("<a title='a&amp;b' href='?x=1&y=2'>link</a>").select("a").attr("href"));
}

@Test
public void parsesSelfClosingStartTagsWithoutAffectingFollowingContent() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<div><img src='x'/><p>after</p></div>");

    org.junit.Assert.assertEquals(1, document.select("img").size());
    org.junit.Assert.assertEquals("after", document.select("p").text());
}
}
