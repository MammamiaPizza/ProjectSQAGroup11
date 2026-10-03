package org.jsoup.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DuplicateAttributeHandlingTest {

    @Test
    public void htmlDefaultDropsCaseInsensitiveDuplicatesAndKeepsFirstValue() {
        Document document = Parser.htmlParser().parseInput(
            "<p one=One one=Two two=two one=Three two=Five>Text</p>", "");

        Element paragraph = document.body().child(0);
        assertEquals("<p one=\"One\" two=\"two\">Text</p>", paragraph.outerHtml());
        assertEquals(2, paragraph.attributes().size());
        assertEquals("One", paragraph.attr("one"));
        assertEquals("two", paragraph.attr("two"));
    }

    @Test
    public void caseSensitiveHtmlRetainsDifferentCasesButDropsExactDuplicates() {
        Parser parser = Parser.htmlParser().settings(ParseSettings.preserveCase);
        Document document = parser.parseInput(
            "<p One=One one=Three two=Four One=Two two=Five Two=Six>Text</p>", "");

        Element paragraph = document.body().child(0);
        assertEquals("<p One=\"One\" one=\"Three\" two=\"Four\" Two=\"Six\">Text</p>",
            paragraph.outerHtml());
        assertEquals(4, paragraph.attributes().size());
        assertEquals("One", paragraph.attr("One"));
        assertEquals("Three", paragraph.attr("one"));
        assertEquals("Four", paragraph.attr("two"));
        assertEquals("Six", paragraph.attr("Two"));
    }

    @Test
    public void xmlDropsExactDuplicatesWhileRetainingAllCaseDistinctAttributes() {
        Document document = Parser.xmlParser().parseInput(
            "<p One=One ONE=Two one=Three two=Six One=Four ONE=Five two=Seven Two=Eight>Text</p>", "");

        Element paragraph = document.child(0);
        assertEquals("<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>",
            paragraph.outerHtml());
        assertEquals(5, paragraph.attributes().size());
        assertEquals("One", paragraph.attr("One"));
        assertEquals("Two", paragraph.attr("ONE"));
        assertEquals("Three", paragraph.attr("one"));
        assertEquals("Six", paragraph.attr("two"));
        assertEquals("Eight", paragraph.attr("Two"));
    }

    @Test
    public void parseSettingsTrimAndNormalizeNamesAccordingToCasePolicy() {
        assertEquals("mixed", ParseSettings.htmlDefault.normalizeTag(" MiXeD "));
        assertEquals("mixed", ParseSettings.htmlDefault.normalizeAttribute(" MiXeD "));
        assertEquals("MiXeD", ParseSettings.preserveCase.normalizeTag(" MiXeD "));
        assertEquals("MiXeD", ParseSettings.preserveCase.normalizeAttribute(" MiXeD "));
    }
}