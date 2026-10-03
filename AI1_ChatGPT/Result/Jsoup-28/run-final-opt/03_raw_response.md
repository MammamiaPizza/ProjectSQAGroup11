package org.jsoup.nodes;

import org.jsoup.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesRegressionTest {

    @Test
    public void relaxedUnescapeDecodesBaseEntitiesButNotSemicolonlessExtendedEntities() {
        assertEquals("Å &angst Å",
                Entities.unescape("&Aring &angst &angst;", false));
    }

    @Test
    public void strictUnescapeRequiresSemicolonsForNamedEntities() {
        assertEquals("& &angst Å",
                Entities.unescape("&amp; &angst &angst;", true));
    }

    @Test
    public void relaxedAndStrictUnescapeHandleNumericReferencesAccordingToSemicolonRequirement() {
        assertEquals("&#65 A", Entities.unescape("&#65 &#x41;", false));
        assertEquals("&#65 A", Entities.unescape("&#65 &#65;", true));
    }

    @Test
    public void unescapeDoesNotDecodeExtendedEntityPrefixesInsideAttributeLikeText() {
        assertEquals("http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2",
                Entities.unescape("http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2", false));
    }

    @Test
    public void parserDoesNotUseShortestNamedEntityPrefixInText() {
        assertEquals("One &clubsuite; ♣",
                Parser.parse("<p>One &clubsuite; &clubsuit;</p>", "").body().text());
    }

    @Test
    public void parserPreservesSemicolonlessExtendedEntityPrefixesInAttributeValues() {
        String html = "<a href='http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2'>link</a>";
        assertEquals("http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2",
                Parser.parse(html, "").body().getElementsByTag("a").first().attr("href"));
    }

    @Test
    public void parserDoesNotDecodeAttributeReferencesFollowedByEqualsOrWithoutRequiredSemicolon() {
        String html = "<a href='?foo=bar&mid&lt=true'>link</a>";
        assertEquals("?foo=bar&mid&lt=true",
                Parser.parse(html, "").body().getElementsByTag("a").first().attr("href"));
    }

    @Test
    public void parserDecodesCompleteSemicolonTerminatedReferences() {
        assertEquals("& Å ©",
                Parser.parse("<p>&amp; &angst; &#169;</p>", "").body().text());
    }
}