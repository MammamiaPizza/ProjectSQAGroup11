package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeBuilderStateRegressionTest {
    @Test
    public void retainsStandaloneZeroCharacterAsText() {
        Document document = Jsoup.parse("<p>0</p>");

        assertEquals("0", document.select("p").text());
    }

    @Test
    public void retainsZeroCharacterAfterNestedElementText() {
        Document document = Jsoup.parse("<p>before<b>middle</b>0</p>");

        assertEquals("beforemiddle0", document.select("p").text());
    }

    @Test
    public void retainsZeroCharacterBeforeNestedElementText() {
        Document document = Jsoup.parse("<p>0<b>middle</b>after</p>");

        assertEquals("0middleafter", document.select("p").text());
    }
}
