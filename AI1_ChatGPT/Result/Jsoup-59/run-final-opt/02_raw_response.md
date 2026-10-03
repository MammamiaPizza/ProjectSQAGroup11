package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TokenControlCharacterTest {

    @Test
    public void finaliseTagIgnoresControlOnlyAttributeName() {
        Token.StartTag tag = new Token.StartTag();
        tag.appendAttributeName((char) 0x01);

        tag.finaliseTag();

        assertEquals(0, tag.getAttributes().size());
    }

    @Test
    public void newAttributeSkipsControlOnlyNameAndAllowsFollowingAttribute() {
        Token.StartTag tag = new Token.StartTag();
        tag.appendAttributeName((char) 0x1f);
        tag.newAttribute();

        tag.appendAttributeName("title");
        tag.appendAttributeValue("example");
        tag.finaliseTag();

        assertEquals(1, tag.getAttributes().size());
        assertTrue(tag.getAttributes().hasKey("title"));
        assertEquals("example", tag.getAttributes().get("title"));
    }

    @Test
    public void parserHandlesControlCharacterInAttributeName() {
        Document document = Jsoup.parse("<div " + (char) 0x01 + " title=value>text</div>");

        assertEquals("value", document.select("div").first().attr("title"));
        assertEquals(1, document.select("div").first().attributes().size());
        assertEquals("text", document.select("div").first().text());
    }

    @Test
    public void cleanerHandlesControlCharacterAfterTagName() {
        Document dirty = Jsoup.parse("<p " + (char) 0x1f + ">One</p>");

        Document cleaned = new Cleaner(Whitelist.none()).clean(dirty);

        assertEquals("One", cleaned.body().text());
    }

    @Test
    public void normalAttributesAndValuesArePreservedAcrossNewAttribute() {
        Token.StartTag tag = new Token.StartTag();

        tag.appendAttributeName("first");
        tag.appendAttributeValue("one");
        tag.newAttribute();

        tag.appendAttributeName("second");
        tag.appendAttributeValue('t');
        tag.appendAttributeValue('w');
        tag.appendAttributeValue('o');
        tag.finaliseTag();

        assertEquals(2, tag.getAttributes().size());
        assertEquals("one", tag.getAttributes().get("first"));
        assertEquals("two", tag.getAttributes().get("second"));
    }

    @Test
    public void explicitEmptyAndBooleanAttributesRemainDistinctEntries() {
        Token.StartTag tag = new Token.StartTag();

        tag.appendAttributeName("empty");
        tag.setEmptyAttributeValue();
        tag.newAttribute();

        tag.appendAttributeName("checked");
        tag.finaliseTag();

        assertEquals(2, tag.getAttributes().size());
        assertTrue(tag.getAttributes().hasKey("empty"));
        assertTrue(tag.getAttributes().hasKey("checked"));
        assertEquals("", tag.getAttributes().get("empty"));
        assertEquals("", tag.getAttributes().get("checked"));
    }

    @Test
    public void tagNameAppendMaintainsOriginalAndNormalizedNames() {
        Token.StartTag tag = new Token.StartTag();

        tag.appendTagName("Di");
        tag.appendTagName('V');

        assertEquals("DiV", tag.name());
        assertEquals("div", tag.normalName());
    }

    @Test
    public void resetClearsTagStateAndRestoresStartTagAttributes() {
        Token.StartTag tag = new Token.StartTag();
        tag.name("DIV");
        tag.appendAttributeName("id");
        tag.appendAttributeValue("x");
        tag.finaliseTag();

        tag.reset();

        assertNull(tag.normalName());
        assertNotNull(tag.getAttributes());
        assertEquals(0, tag.getAttributes().size());
        assertFalse(tag.isSelfClosing());
    }
}