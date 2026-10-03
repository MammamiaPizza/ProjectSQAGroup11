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

@org.junit.Test
public void parsesScriptDataUntilAnAppropriateEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script>var x = 1;</script><p>after</p>");

    org.junit.Assert.assertEquals("var x = 1;", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void retainsNonMatchingScriptEndTagAsScriptData() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script>a</scriptx>b</script><p>after</p>");

    org.junit.Assert.assertEquals("a</scriptx>b", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void retainsEscapedNestedScriptContentUntilOuterEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script><!--<script>foo</script>bar--></script><p>after</p>");

    org.junit.Assert.assertEquals("<!--<script>foo</script>bar-->", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void closesEscapedScriptAfterNonScriptDoubleEscapeCandidate() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script><!--<style>foo</script><p>after</p>");

    org.junit.Assert.assertEquals("<!--<style>foo", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}
}
