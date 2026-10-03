package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharacterReferenceDigitSuffixTest {
    @Test
    public void letterDigitEntityNamesMustNotMatchShorterNamedPrefixes() {
        assertEquals(
            "&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;",
            Entities.unescape("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;")
        );
    }

    @Test
    public void digitSuffixedReferenceRemainsLiteralWhenAdjacentToValidReference() {
        assertEquals(
            "start &sup1; middle & end",
            Entities.unescape("start &sup1; middle &amp; end")
        );
    }

    @Test
    public void validShortNamedEntityStillDecodes() {
        assertEquals("\u2283", Entities.unescape("&sup;"));
    }

    @Test
    public void numericCharacterReferencesStillDecode() {
        assertEquals("AA", Entities.unescape("&#65;&#x41;"));
    }

    @Test
    public void digitSuffixedNamesRemainLiteralInParsedText() {
        assertEquals(
            "&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;",
            Jsoup.parse("<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>").text()
        );
    }

    @Test
    public void digitSuffixedNamesRemainLiteralInAttributes() {
        Element paragraph = Jsoup.parse("<p title='&sup1;&frac14;'>text</p>").select("p").first();

        assertEquals("&sup1;&frac14;", paragraph.attr("title"));
    }
}
