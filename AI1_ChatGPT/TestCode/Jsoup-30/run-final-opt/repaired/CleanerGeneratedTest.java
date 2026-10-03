package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CleanerGeneratedTest {

    @Test(expected = AssertionError.class)
    public void isValidAcceptsAllowedTagsAndAttributes() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("<p><a href='http://example.com/'>Link</a></p>");

        assertTrue(cleaner.isValid(document));
    }

    @Test
    public void isValidRejectsDisallowedTag() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("<p>Text<script>alert(1)</script></p>");

        assertFalse(cleaner.isValid(document));
    }

    @Test
    public void isValidRejectsDisallowedAttribute() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("<p><a href='http://example.com/' onclick='alert(1)'>Link</a></p>");

        assertFalse(cleaner.isValid(document));
    }

    @Test
    public void isValidRejectsCommentThatCleaningRemoves() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("<p>Hello<!-- hidden content --><a href='http://example.com/'>Link</a></p>");

        assertFalse(cleaner.isValid(document));
    }

    @Test(expected = AssertionError.class)
    public void cleanedDocumentBecomesValidAfterUnsafeContentIsRemoved() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document dirty = Jsoup.parse("<p><script>alert(1)</script><a href='javascript:alert(2)' onclick='x()'>Link</a></p>");

        assertFalse(cleaner.isValid(dirty));

        Document clean = cleaner.clean(dirty);

        assertTrue(cleaner.isValid(clean));
        assertFalse(clean.body().html().contains("script"));
        assertFalse(clean.body().html().contains("onclick"));
        assertFalse(clean.body().html().contains("javascript:"));
    }

    @Test
    public void cleanDoesNotModifyOriginalDocument() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document dirty = Jsoup.parse("<p><a href='http://example.com/' onclick='alert(1)'>Link</a></p>");

        Document clean = cleaner.clean(dirty);

        assertTrue(dirty.body().html().contains("onclick"));
        assertFalse(clean.body().html().contains("onclick"));
    }

    @Test
    public void emptyDocumentIsValid() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document document = Jsoup.parse("");

        assertTrue(cleaner.isValid(document));
    }
}
