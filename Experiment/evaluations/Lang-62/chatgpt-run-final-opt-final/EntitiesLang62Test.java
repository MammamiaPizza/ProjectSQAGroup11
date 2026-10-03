package org.apache.commons.lang;

import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class EntitiesLang62Test {

    @Test
    public void unescapeReturnsOriginalStringWhenNoEntityIsPresent() {
        String text = "plain text 123";

        assertSame(text, Entities.HTML40.unescape(text));
    }

    @Test
    public void unescapeResolvesNamedAndDecimalEntities() {
        assertEquals("Fish & Chips < ©",
                Entities.HTML40.unescape("Fish &amp; Chips &lt; &#169;"));
    }

    @Test
    public void unescapeAcceptsMaximumCharacterValue() {
        assertEquals(String.valueOf(Character.MAX_VALUE),
                Entities.HTML40.unescape("&#65535;"));
    }

    @Test
    public void unescapeLeavesValuesBeyondCharacterRangeUnchanged() {
        assertEquals("before &#65536; after",
                Entities.HTML40.unescape("before &#65536; after"));
    }

    @Test
    public void unescapeLeavesLargeButParseableNumericValueUnchanged() {
        assertEquals("&#12345678;", Entities.HTML40.unescape("&#12345678;"));
    }

    @Test
    public void unescapeLeavesIntegerOverflowAndMalformedReferencesUnchanged() {
        assertEquals("&#2147483648; &#; &#x; &#12",
                Entities.HTML40.unescape("&#2147483648; &#; &#x; &#12"));
    }

    @Test
    public void writerUnescapeResolvesNormalEntities() throws Exception {
        StringWriter writer = new StringWriter();

        Entities.HTML40.unescape(writer, "A&amp;B&#169;");

        assertEquals("A&B©", writer.toString());
    }

    @Test
    public void writerUnescapeLeavesValuesBeyondCharacterRangeUnchanged() throws Exception {
        StringWriter writer = new StringWriter();

        Entities.HTML40.unescape(writer, "&#12345678;");

        assertEquals("&#12345678;", writer.toString());
    }

@org.junit.Test
public void escapeUsesNamedEntitiesNumericEntitiesAndLiteralCharacters() {
    org.junit.Assert.assertEquals("&amp;&lt;&#128;A",
            Entities.HTML40.escape("&<\u0080A"));
}

@org.junit.Test
public void writerEscapeUsesNamedEntitiesNumericEntitiesAndLiteralCharacters()
        throws java.io.IOException {
    java.io.StringWriter writer = new java.io.StringWriter();

    Entities.HTML40.escape(writer, "\u0080&x");

    org.junit.Assert.assertEquals("&#128;&amp;x", writer.toString());
}
}
