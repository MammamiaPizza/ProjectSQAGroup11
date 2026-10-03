package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharacterReaderAndTokeniserStateBug83Test {

    @Test
    public void consumeTagNameStopsBeforeLessThanSoItCanStartAnotherTag() {
        CharacterReader reader = new CharacterReader("a<p>");

        assertEquals("a", reader.consumeTagName());
        assertEquals('<', reader.current());
    }

    @Test
    public void consumeToLeavesTheMatchedCharacterUnread() {
        CharacterReader reader = new CharacterReader("prefix<lemma");

        assertEquals("prefix", reader.consumeTo('<'));
        assertEquals('<', reader.current());
        assertEquals(6, reader.pos());
    }

    @Test
    public void consumeToAnyStopsAtFirstMatchingDelimiter() {
        CharacterReader reader = new CharacterReader("name=value>tail");

        assertEquals("name", reader.consumeToAny('=', '>'));
        assertEquals('=', reader.current());

        reader.advance();
        assertEquals("value", reader.consumeToAny('>', '<'));
        assertEquals('>', reader.current());
    }

    @Test
    public void consumeToCanReachLessThanAfterReaderBufferRefillWithoutSkippingIt() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 40000; i++) {
            input.append('a');
        }
        input.append("<p>");

        CharacterReader reader = new CharacterReader(input.toString());
        StringBuilder consumed = new StringBuilder();

        for (int i = 0; i < 4 && reader.current() != '<'; i++) {
            String part = reader.consumeTo('<');
            assertTrue("Reader must make progress while searching across buffers", part.length() > 0);
            consumed.append(part);
        }

        assertEquals(40000, consumed.length());
        assertEquals('<', reader.current());
        assertEquals(40000, reader.pos());
    }

    @Test
    public void roughAttributesWithLessThanStartFollowingParagraphAndAnchorTags() {
        String html = "<p =a>One<a <p>Something</a></p><a <p>Else</a>";

        assertEquals(
            "<p =a>One<a></a></p><p><a>Something</a></p><a>Else</a>",
            bodyHtml(html)
        );
    }

    @Test
    public void lessThanWhileParsingTagAttributesStartsNewTags() {
        String html = "<p <p<div id=\"one\" <span>Two</p>";

        assertEquals(
            "<p></p><p></p><div id=\"one\"><span>Two</span></div>",
            bodyHtml(html)
        );
    }

    @Test
    public void lessThanInTagNameStartsAnotherParagraphInsteadOfBecomingPartOfTheName() {
        String html = "<p<p>One</p>";

        assertEquals("<p></p><p>One</p>", bodyHtml(html));
    }

    @Test
    public void lessThanAfterAnAttributeSeparatesTheContainingAndNestedTags() {
        String html = "<div id=\"one\" <span>Two</span></div>";

        assertEquals("<div id=\"one\"><span>Two</span></div>", bodyHtml(html));
    }

    private String bodyHtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().prettyPrint(false);
        return document.body().html();
    }
}
