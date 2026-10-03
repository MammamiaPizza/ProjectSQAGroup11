package org.jsoup.safety;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class CleanerTest {

 @Test
 public void handlesFramesets() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<html><head></head><frameset><frame src=\"foo\" /><frame
src=\"bar\" /></frameset></html>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean);
     assertNotNull(clean.body());
     assertTrue(clean.body().childNodes().isEmpty());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void cleansDocumentWithOnlyFrameset() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<html><head></head><frameset></frameset></html>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertTrue(clean.body().childNodes().isEmpty());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void framesetWithNoframesKeepsFallbackText() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<html><head></head><frameset><frame src=\"foo\"
/><noframes><p>fallback</p></noframes></frameset></html>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertEquals("fallback", clean.body().text());
     assertEquals(0, clean.body().getElementsByTag("frameset").size());
     assertEquals(0, clean.body().getElementsByTag("noframes").size());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void framesetWithDisallowedTagKeepsSafeText() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<html><head></head><frameset><frame src=\"foo\"
/><div>unwanted</div></frameset></html>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertEquals(0, clean.body().getElementsByTag("frameset").size());
     assertEquals(0, clean.body().getElementsByTag("frame").size());
     assertEquals(0, clean.body().getElementsByTag("div").size());
     assertEquals("unwanted", clean.body().text());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void bodyAfterFramesetKeepsAllowedTags() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<html><head></head><frameset><frame src=\"foo\"
/></frameset><body><p>content</p></body></html>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertEquals("content", clean.body().text());
     assertEquals(0, clean.body().getElementsByTag("frameset").size());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void emptyDocumentDoesNotFail() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertTrue(clean.body().childNodes().isEmpty());
     assertTrue(cleaner.isValid(clean));
 }

 @Test
 public void cleansAllowedBodyContent() {
     Cleaner cleaner = new Cleaner(Whitelist.basic());
     Document dirty = Jsoup.parse("<p>Hello <b>world</b></p>");

     Document clean = cleaner.clean(dirty);

     assertNotNull(clean.body());
     assertEquals("Hello world", clean.body().text());
     assertEquals(1, clean.body().getElementsByTag("p").size());
     assertEquals(1, clean.body().getElementsByTag("b").size());
     assertTrue(cleaner.isValid(clean));
 }

}