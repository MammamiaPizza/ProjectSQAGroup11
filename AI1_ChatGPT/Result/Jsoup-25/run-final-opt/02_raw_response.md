package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TagWhitespaceTest {

    @Test
    public void textareaIsKnownInlineTagThatPreservesWhitespace() {
        Tag textarea = Tag.valueOf("textarea");

        assertEquals("textarea", textarea.getName());
        assertTrue(textarea.isKnownTag());
        assertTrue(textarea.isInline());
        assertTrue(textarea.preserveWhitespace());
    }

    @Test
    public void textareaLookupIsCaseInsensitiveAndTrimmed() {
        Tag canonical = Tag.valueOf("textarea");
        Tag normalized = Tag.valueOf("  TEXTAREA  ");

        assertSame(canonical, normalized);
        assertTrue(normalized.preserveWhitespace());
    }

    @Test
    public void parserPreservesNewlineInsideTextareaText() {
        Document document = Jsoup.parse("<textarea>One\nTwo</textarea>");
        Element textarea = document.select("textarea").first();

        assertNotNull(textarea);
        assertEquals("One\nTwo", textarea.text());
    }

    @Test
    public void ordinaryKnownTagDoesNotPreserveWhitespace() {
        Tag div = Tag.valueOf("div");

        assertTrue(div.isKnownTag());
        assertFalse(div.preserveWhitespace());
        assertTrue(div.isBlock());
    }

    @Test
    public void preStillPreservesWhitespace() {
        Tag pre = Tag.valueOf("pre");

        assertTrue(pre.isKnownTag());
        assertTrue(pre.preserveWhitespace());
    }

    @Test
    public void unknownTagIsNotRegisteredAsKnown() {
        Tag unknown = Tag.valueOf("custom-widget");

        assertEquals("custom-widget", unknown.getName());
        assertFalse(unknown.isKnownTag());
        assertTrue(unknown.isInline());
        assertTrue(unknown.canContainBlock());
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfRejectsNullName() {
        Tag.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfRejectsBlankName() {
        Tag.valueOf("   ");
    }
}