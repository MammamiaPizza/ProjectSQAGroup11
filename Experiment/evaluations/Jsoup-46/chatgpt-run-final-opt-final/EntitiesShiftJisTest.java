package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntitiesShiftJisTest {
    private static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");

    private Document.OutputSettings shiftJisSettings() {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.charset(SHIFT_JIS);
        return settings;
    }

    @Test
    public void escapePreservesShiftJisRepresentableJapaneseCharacters() {
        String text = "日本語あいうえお漢字";
        CharsetEncoder encoder = SHIFT_JIS.newEncoder();

        assertTrue(encoder.canEncode(text));

        String escaped = Entities.escape(text, shiftJisSettings());

        assertEquals(text, escaped);
        assertFalse(escaped.contains("?"));
    }

    @Test
    public void escapeKeepsRepresentableTextWhileEscapingRequiredHtmlCharacters() {
        String text = "ASCII & < > \" 日本語";
        CharsetEncoder encoder = SHIFT_JIS.newEncoder();

        assertTrue(encoder.canEncode("日本語"));

        String escaped = Entities.escape(text, shiftJisSettings());

        assertTrue(escaped.contains("ASCII"));
        assertTrue(escaped.contains("&amp;"));
        assertTrue(escaped.contains("&lt;"));
        assertTrue(escaped.contains("&gt;"));
        assertTrue(escaped.contains("\""));
        assertTrue(escaped.contains("日本語"));
        assertFalse(escaped.contains("?"));
    }

    @Test
    public void escapeUsesAnHtmlEscapeForCharactersNotEncodableInShiftJis() {
        String text = "before \u2603 after";
        CharsetEncoder encoder = SHIFT_JIS.newEncoder();

        assertFalse(encoder.canEncode('\u2603'));

        String escaped = Entities.escape(text, shiftJisSettings());

        assertTrue(escaped.startsWith("before "));
        assertTrue(escaped.endsWith(" after"));
        assertFalse(escaped.contains("\u2603"));
        assertFalse(escaped.contains("?"));
        assertEquals(escaped, new String(escaped.getBytes(SHIFT_JIS), SHIFT_JIS));
    }

    @Test
    public void documentSerializationWithShiftJisDoesNotReplaceJapaneseText() {
        String japanese = "日本語の内容";
        Document document = Jsoup.parse("<html><head></head><body><p>" + japanese + "</p></body></html>");
        document.outputSettings().charset(SHIFT_JIS);

        String html = document.outerHtml();

        assertTrue(html.contains(japanese));
        assertFalse(html.contains("?"));
    }

@org.junit.Test
public void namedEntityLookupRecognizesKnownAndUnknownNames() {
    org.junit.Assert.assertTrue(Entities.isNamedEntity("amp"));
    org.junit.Assert.assertTrue(Entities.isBaseNamedEntity("amp"));
    org.junit.Assert.assertEquals(Character.valueOf('&'), Entities.getCharacterByName("amp"));
    org.junit.Assert.assertFalse(Entities.isNamedEntity("notAnEntity"));
    org.junit.Assert.assertFalse(Entities.isBaseNamedEntity("notAnEntity"));
    org.junit.Assert.assertNull(Entities.getCharacterByName("notAnEntity"));
}

@org.junit.Test
public void escapeModeMapsContainTheAmpersandEntity() {
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.xhtml.getMap().get('&'));
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.base.getMap().get('&'));
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.extended.getMap().get('&'));
}

@org.junit.Test
public void unescapeDecodesStandardNamedEntitiesInBothModes() {
    org.junit.Assert.assertEquals("<>&", Entities.unescape("&lt;&gt;&amp;"));
    org.junit.Assert.assertEquals("<>&", Entities.unescape("&lt;&gt;&amp;", true));
}
}
