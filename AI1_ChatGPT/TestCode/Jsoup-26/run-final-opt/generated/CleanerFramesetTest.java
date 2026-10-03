package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CleanerFramesetTest {

    @Test
    public void cleanHandlesParsedFramesetDocumentWithoutBody() {
        Whitelist whitelist = Whitelist.none()
                .addTags("frameset", "frame")
                .addAttributes("frameset", "cols")
                .addAttributes("frame", "src", "name");
        Document dirty = Jsoup.parse("<html><head><title>Frames</title></head>"
                + "<frameset cols=\"*\"><frame src=\"one.html\" name=\"main\"></frameset></html>");

        Document clean = new Cleaner(whitelist).clean(dirty);

        assertNotNull(clean.body());
        assertEquals(0, clean.body().childNodeSize());
    }

    @Test
    public void isValidHandlesParsedFramesetDocumentWithoutBody() {
        Whitelist whitelist = Whitelist.none()
                .addTags("frameset", "frame")
                .addAttributes("frameset", "cols")
                .addAttributes("frame", "src", "name");
        Document dirty = Jsoup.parse("<frameset cols=\"*\"><frame src=\"one.html\" name=\"main\"></frameset>");

        assertTrue(new Cleaner(whitelist).isValid(dirty));
    }

    @Test
    public void cleanRetainsWhitelistedFramesetAndFrameAttributesInBody() {
        Whitelist whitelist = Whitelist.none()
                .addTags("frameset", "frame")
                .addAttributes("frameset", "cols")
                .addAttributes("frame", "src", "name");
        Document dirty = Document.createShell("");
        Element frameset = dirty.body().appendElement("frameset").attr("cols", "25%,75%");
        frameset.appendElement("frame").attr("src", "left.html").attr("name", "left");

        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        Element cleanFrameset = clean.body().getElementsByTag("frameset").get(0);
        Element cleanFrame = clean.body().getElementsByTag("frame").get(0);

        assertEquals("25%,75%", cleanFrameset.attr("cols"));
        assertEquals("left.html", cleanFrame.attr("src"));
        assertEquals("left", cleanFrame.attr("name"));
        assertTrue(cleaner.isValid(dirty));
    }

    @Test
    public void cleanRemovesUnsafeFrameAttributesAndMarksDocumentInvalid() {
        Whitelist whitelist = Whitelist.none()
                .addTags("frameset", "frame")
                .addAttributes("frame", "src");
        Document dirty = Document.createShell("");
        dirty.body().appendElement("frameset")
                .appendElement("frame")
                .attr("src", "safe.html")
                .attr("onclick", "alert(1)");

        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        Element cleanFrame = clean.body().getElementsByTag("frame").get(0);

        assertEquals("safe.html", cleanFrame.attr("src"));
        assertFalse(cleanFrame.hasAttr("onclick"));
        assertFalse(cleaner.isValid(dirty));
    }

    @Test
    public void cleanPreservesSafeDescendantsAndTextInsideUnsafeContainer() {
        Whitelist whitelist = Whitelist.none().addTags("p");
        Document dirty = Jsoup.parse("<div>before<p>kept</p>after</div>");

        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);

        assertEquals(1, clean.body().getElementsByTag("p").size());
        assertEquals("kept", clean.body().getElementsByTag("p").get(0).text());
        assertEquals("before kept after", clean.body().text());
        assertFalse(cleaner.isValid(dirty));
    }
}
