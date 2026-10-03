package org.jsoup.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HtmlTreeBuilderStateTest {

    private Document parsePreservingTagCase(String html) {
        Parser parser = Parser.htmlParser();
        parser.settings(ParseSettings.preserveCase);
        return parser.parseInput(html, "");
    }

    @Test
    public void closesMixedCaseElementWithMatchingMixedCaseEndTag() {
        Document document = parsePreservingTagCase("<r><X>A</X><y>B</y></r>");

        Element r = document.body().child(0);
        assertEquals("r", r.nodeName());
        assertEquals(2, r.children().size());

        Element upperCase = r.child(0);
        Element lowerCase = r.child(1);
        assertEquals("X", upperCase.nodeName());
        assertEquals("A", upperCase.text());
        assertEquals("y", lowerCase.nodeName());
        assertEquals("B", lowerCase.text());
    }

    @Test
    public void followingElementIsSiblingAfterMixedCaseEndTag() {
        Document document = parsePreservingTagCase("<r><X>A</X><y>B</y></r>");

        Element r = document.body().child(0);
        Element upperCase = r.child(0);
        Element followingY = r.child(1);

        assertSame(r, upperCase.parent());
        assertSame(r, followingY.parent());
        assertEquals(0, upperCase.children().size());
    }

    @Test
    public void preservesCorrectStackForNestedMixedCaseElements() {
        Document document = parsePreservingTagCase("<r><X><Z>A</Z></X><y>B</y></r>");

        Element r = document.body().child(0);
        assertEquals(2, r.children().size());

        Element upperCase = r.child(0);
        assertEquals("X", upperCase.nodeName());
        assertEquals(1, upperCase.children().size());
        assertEquals("Z", upperCase.child(0).nodeName());
        assertEquals("A", upperCase.child(0).text());

        Element y = r.child(1);
        assertEquals("y", y.nodeName());
        assertEquals("B", y.text());
    }

    @Test
    public void lowerCaseTagsContinueToCloseNormallyWithPreservedCaseSettings() {
        Document document = parsePreservingTagCase("<r><x>A</x><y>B</y></r>");

        Element r = document.body().child(0);
        assertEquals(2, r.children().size());
        assertEquals("x", r.child(0).nodeName());
        assertEquals("A", r.child(0).text());
        assertEquals("y", r.child(1).nodeName());
        assertEquals("B", r.child(1).text());
    }
}