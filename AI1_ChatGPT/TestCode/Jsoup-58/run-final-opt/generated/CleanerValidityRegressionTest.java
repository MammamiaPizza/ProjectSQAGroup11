package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CleanerValidityRegressionTest {

    @Test
    public void validWhitelistedBodyHtmlIsValid() {
        assertTrue(Jsoup.isValid("<p>Hello <b>world</b></p>", Whitelist.basic()));
    }

    @Test
    public void bodyHtmlWithDisallowedTagIsInvalid() {
        assertFalse(Jsoup.isValid("<p>Hello<script>alert(1)</script></p>", Whitelist.basic()));
    }

    @Test
    public void bodyHtmlWithDisallowedAttributeIsInvalid() {
        assertFalse(Jsoup.isValid("<p onclick='alert(1)'>Hello</p>", Whitelist.basic()));
    }

    @Test
    public void parsedDocumentWithOnlySafeBodyContentIsValid() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("<html><head></head><body><p>Safe <b>content</b></p></body></html>");

        assertTrue(cleaner.isValid(document));
    }

    @Test
    public void documentWithHeadContentIsInvalidEvenWhenBodyIsSafe() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse(
                "<html><head><title>Untrusted title</title></head><body><p>Safe content</p></body></html>");

        assertFalse(cleaner.isValid(document));
    }

    @Test
    public void emptyBodyHtmlIsValid() {
        assertTrue(Jsoup.isValid("", Whitelist.basic()));
    }

    @Test
    public void cleaningRemovesUnsafeContentAndRetainsSafeContent() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document dirty = Jsoup.parseBodyFragment(
                "<p>Good <b>text</b><script>alert(1)</script></p>", "");

        Document clean = cleaner.clean(dirty);

        assertEquals(0, clean.select("script").size());
        assertEquals("Good text", clean.select("p").text());
    }

    @Test
    public void parseBodyFragmentPreservesMultipleTopLevelNodes() {
        Document document = Parser.parseBodyFragment("<p>one</p><p>two</p>", "");

        assertEquals(2, document.body().children().size());
        assertEquals("one two", document.body().text());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cleanerRejectsNullDocumentForValidation() {
        new Cleaner(Whitelist.basic()).isValid(null);
    }
}
