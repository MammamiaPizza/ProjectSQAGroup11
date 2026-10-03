package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class XmlTreeBuilderEndTagTest {

    @Test
    public void preservesHierarchyForMatchingTags() {
        Document document = Jsoup.parse("<root><child>text</child><empty /></root>", "", Parser.xmlParser());

        Element root = document.child(0);
        assertEquals("root", root.tagName());
        assertEquals(2, root.childrenSize());
        assertEquals("child", root.child(0).tagName());
        assertEquals("text", root.child(0).text());
        assertEquals("empty", root.child(1).tagName());
    }

    @Test
    public void discordantEndTagClosesInterveningOpenElements() {
        Document document = Jsoup.parse(
                "<root><outer><inner>value</outer><after>next</after></inner><last /></root>",
                "", Parser.xmlParser());

        Element root = document.child(0);
        Element outer = root.child(0);

        assertEquals(3, root.childrenSize());
        assertEquals("outer", outer.tagName());
        assertEquals(1, outer.childrenSize());
        assertEquals("inner", outer.child(0).tagName());
        assertEquals("value", outer.child(0).text());
        assertEquals("after", root.child(1).tagName());
        assertEquals("next", root.child(1).text());
        assertEquals("last", root.child(2).tagName());
    }

    @Test
    public void unmatchedEndTagDoesNotChangeCurrentOpenElement() {
        Document document = Jsoup.parse(
                "<root><open>text</missing><next /></open><tail /></root>",
                "", Parser.xmlParser());

        Element root = document.child(0);
        Element open = root.child(0);

        assertEquals(2, root.childrenSize());
        assertEquals("open", open.tagName());
        assertEquals("text", open.ownText());
        assertEquals(1, open.childrenSize());
        assertEquals("next", open.child(0).tagName());
        assertEquals("tail", root.child(1).tagName());
    }

    @Test
    public void closesNearestMatchingElementWhenTagNamesRepeat() {
        Document document = Jsoup.parse(
                "<root><item><item>inner</item><after /></item><tail /></root>",
                "", Parser.xmlParser());

        Element root = document.child(0);
        Element outerItem = root.child(0);

        assertEquals(2, root.childrenSize());
        assertEquals("item", outerItem.tagName());
        assertEquals(2, outerItem.childrenSize());
        assertEquals("item", outerItem.child(0).tagName());
        assertEquals("inner", outerItem.child(0).text());
        assertEquals("after", outerItem.child(1).tagName());
        assertEquals("tail", root.child(1).tagName());
    }

    @Test
    public void xmlEndTagsAreCaseSensitiveAndDoNotCloseDifferentlyCasedElements() {
        Document document = Jsoup.parse(
                "<Root><Child>value</root><After /></Child><Tail /></Root>",
                "", Parser.xmlParser());

        Element root = document.child(0);
        Element child = root.child(0);

        assertEquals("Root", root.tagName());
        assertEquals(2, root.childrenSize());
        assertEquals("Child", child.tagName());
        assertEquals("value", child.ownText());
        assertEquals(1, child.childrenSize());
        assertEquals("After", child.child(0).tagName());
        assertEquals("Tail", root.child(1).tagName());
    }

    @Test
    public void selfClosingElementDoesNotBecomeCurrentElement() {
        Document document = Jsoup.parse("<root><leaf /><after>ok</after></root>", "", Parser.xmlParser());

        Element root = document.child(0);

        assertEquals(2, root.childrenSize());
        assertEquals("leaf", root.child(0).tagName());
        assertEquals("after", root.child(1).tagName());
        assertEquals("ok", root.child(1).text());
        assertSame(root, root.child(0).parent());
        assertSame(root, root.child(1).parent());
    }
}