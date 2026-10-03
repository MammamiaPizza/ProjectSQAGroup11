package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Test;

public class EntitiesBug4Test {
    private CharsetEncoder asciiEncoder() {
        return Charset.forName("US-ASCII").newEncoder();
    }

    @Test
    public void escapesMixedTextUsingCaseCorrectBaseEntities() {
        String text = "Hello &<> \u00c5 \u00e5 \u03c0 \u65b0";

        assertEquals(
            "Hello &amp;&lt;&gt; &Aring; &aring; &#960; &#26032;",
            Entities.escape(text, asciiEncoder(), Entities.EscapeMode.base)
        );
    }

    @Test
    public void preservesEncodableUnicodeWhileStillEscapingBaseEntities() {
        String text = "Hello &<> \u00c5 \u00e5 \u03c0 \u65b0";
        CharsetEncoder utf8 = Charset.forName("UTF-8").newEncoder();

        assertEquals(
            "Hello &amp;&lt;&gt; &Aring; &aring; \u03c0 \u65b0",
            Entities.escape(text, utf8, Entities.EscapeMode.base)
        );
    }

    @Test
    public void usesDistinctUppercaseAndLowercaseNamedEntities() {
        String text = "\u00dc \u00fc & \u00e4 \u00c4";

        assertEquals(
            "&Uuml; &uuml; &amp; &auml; &Auml;",
            Entities.escape(text, asciiEncoder(), Entities.EscapeMode.base)
        );
    }

    @Test
    public void extendedModeUsesNamedGreekEntityWhenAvailable() {
        assertEquals(
            "&pi;",
            Entities.escape("\u03c0", asciiEncoder(), Entities.EscapeMode.extended)
        );
    }

    @Test
    public void baseModeUsesNamedEntitiesAndNumericFallback() {
        assertEquals(
            "&amp;&lt;&gt;&quot;&Aring;",
            Entities.escape("&<>\"\u00c5", asciiEncoder(), Entities.EscapeMode.base)
        );
    }
}
