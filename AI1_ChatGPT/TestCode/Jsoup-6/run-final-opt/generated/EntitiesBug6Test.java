package org.jsoup.nodes;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesBug6Test {
    private CharsetEncoder asciiEncoder() {
        return Charset.forName("US-ASCII").newEncoder();
    }

    @Test
    public void escapesMarkupCharactersUsingOutputSettings() {
        Document.OutputSettings settings = new Document.OutputSettings();

        assertEquals("plain &amp; &lt;tag&gt; &quot;quoted&quot;",
                Entities.escape("plain & <tag> \"quoted\"", settings));
    }

    @Test
    public void escapesUnencodableCharactersAsNumericEntitiesInXhtmlMode() {
        assertEquals("snowman: &#9731;",
                Entities.escape("snowman: \u2603", asciiEncoder(), Entities.EscapeMode.xhtml));
    }

    @Test
    public void escapesAmpersandsAnglesAndQuotesInBaseMode() {
        assertEquals("&amp;&lt;&gt;&quot;",
                Entities.escape("&<>\"", asciiEncoder(), Entities.EscapeMode.base));
    }

    @Test
    public void unescapesNamedEntitiesIncludingQuotes() {
        assertEquals("He said \"fish & chips\" <today>.",
                Entities.unescape("He said &quot;fish &amp; chips&quot; &lt;today&gt;."));
    }

    @Test
    public void unescapesDecimalEntityWhoseReplacementIsDollar() {
        assertEquals("$", Entities.unescape("&#36;"));
    }

    @Test
    public void unescapesDecimalEntityWhoseReplacementIsBackslash() {
        assertEquals("\\", Entities.unescape("&#92;"));
    }

    @Test
    public void unescapesLowercaseHexadecimalNumericEntities() {
        assertEquals("A$", Entities.unescape("&#x41;&#x24;"));
    }

    @Test
    public void unescapesNamedEntityWithoutSemicolonAtBoundary() {
        assertEquals("Tom & Jerry", Entities.unescape("Tom &amp Jerry"));
    }

    @Test
    public void leavesUnknownAndMalformedReferencesUnchanged() {
        assertEquals("x &unknown; &#xZZ; &#; &; y",
                Entities.unescape("x &unknown; &#xZZ; &#; &; y"));
    }
}
