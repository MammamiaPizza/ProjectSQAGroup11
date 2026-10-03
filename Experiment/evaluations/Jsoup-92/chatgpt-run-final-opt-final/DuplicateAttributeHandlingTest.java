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
        assertEquals("One", paragraph.attributes().get("One"));
        assertEquals("Three", paragraph.attributes().get("one"));
        assertEquals("Four", paragraph.attributes().get("two"));
        assertEquals("Six", paragraph.attributes().get("Two"));
    }

    @Test
    public void xmlDropsExactDuplicatesWhileRetainingAllCaseDistinctAttributes() {
        Document document = Parser.xmlParser().parseInput(
            "<p One=One ONE=Two one=Three two=Six One=Four ONE=Five two=Seven Two=Eight>Text</p>", "");

        Element paragraph = document.child(0);
        assertEquals("<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>",
            paragraph.outerHtml());
        assertEquals(5, paragraph.attributes().size());
        assertEquals("One", paragraph.attributes().get("One"));
        assertEquals("Two", paragraph.attributes().get("ONE"));
        assertEquals("Three", paragraph.attributes().get("one"));
        assertEquals("Six", paragraph.attributes().get("two"));
        assertEquals("Eight", paragraph.attributes().get("Two"));
    }

    @Test
    public void parseSettingsTrimAndNormalizeNamesAccordingToCasePolicy() {
        assertEquals("mixed", ParseSettings.htmlDefault.normalizeTag(" MiXeD "));
        assertEquals("mixed", ParseSettings.htmlDefault.normalizeAttribute(" MiXeD "));
        assertEquals("MiXeD", ParseSettings.preserveCase.normalizeTag(" MiXeD "));
        assertEquals("MiXeD", ParseSettings.preserveCase.normalizeAttribute(" MiXeD "));
    }

@org.junit.Test
public void attributesAddAllReplacesMatchingKeysAndCopiesNewKeys() {
    org.jsoup.nodes.Attributes attributes = new org.jsoup.nodes.Attributes();
    attributes.put("one", "original");

    org.jsoup.nodes.Attributes incoming = new org.jsoup.nodes.Attributes();
    incoming.put("one", "replacement");
    incoming.put("Two", "second");

    attributes.addAll(incoming);

    org.junit.Assert.assertEquals(2, attributes.size());
    org.junit.Assert.assertEquals("replacement", attributes.get("one"));
    org.junit.Assert.assertEquals("second", attributes.get("Two"));
}

@org.junit.Test
public void clonedAttributesCanBeChangedWithoutChangingOriginal() {
    org.jsoup.nodes.Attributes original = new org.jsoup.nodes.Attributes();
    original.put("first", "one");
    original.put("second", "two");

    org.jsoup.nodes.Attributes clone = original.clone();
    clone.put("first", "changed");
    clone.put("third", "three");

    org.junit.Assert.assertEquals(2, original.size());
    org.junit.Assert.assertEquals("one", original.get("first"));
    org.junit.Assert.assertEquals("", original.get("third"));
    org.junit.Assert.assertEquals(3, clone.size());
    org.junit.Assert.assertEquals("changed", clone.get("first"));
    org.junit.Assert.assertEquals("three", clone.get("third"));
}

@org.junit.Test
public void datasetViewExposesAndUpdatesOnlyDataAttributes() {
    org.jsoup.nodes.Attributes attributes = new org.jsoup.nodes.Attributes();
    attributes.put("data-one", "1");
    attributes.put("title", "plain");

    java.util.Map<String, String> dataset = attributes.dataset();

    org.junit.Assert.assertEquals(1, dataset.size());
    org.junit.Assert.assertEquals("1", dataset.get("one"));
    org.junit.Assert.assertNull(dataset.put("two", "2"));
    org.junit.Assert.assertEquals("2", attributes.get("data-two"));
    org.junit.Assert.assertEquals("1", dataset.put("one", "updated"));
    org.junit.Assert.assertEquals("updated", attributes.get("data-one"));
}
}
