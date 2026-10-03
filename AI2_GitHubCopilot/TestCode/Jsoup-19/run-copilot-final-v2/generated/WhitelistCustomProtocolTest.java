package org.jsoup.safety;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class WhitelistCustomProtocolTest {

     // Bug 127: addProtocols for custom protocols (like "cid") should be preserved
     // when cleaning HTML with that protocol in an allowed attribute like img src.
     @Test
     public void testCustomProtocolPreservedForImgSrc() {
         Whitelist whitelist = Whitelist.basicWithImages()
             .addProtocols("img", "src", "cid");

         String dirty = "<img src=\"cid:12345\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue("Custom cid protocol should be preserved in cleaned output",
             clean.contains("src=\"cid:12345\""));
     }

     @Test
     public void testMultipleCustomProtocolsPreserved() {
         Whitelist whitelist = Whitelist.relaxed()
             .addProtocols("img", "src", "cid", "data");

         String dirty = "<img src=\"cid:foo\" /><img src=\"data:bar\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("cid:foo"));
         assertTrue(clean.contains("data:bar"));
     }

     @Test
     public void testAllTagWildcardWithCustomProtocol() {
         Whitelist whitelist = Whitelist.none()
             .addTags("img")
             .addAttributes(":all", "src")
             .addProtocols("img", "src", "cid");

         String dirty = "<img src=\"cid:abc\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("cid:abc"));
     }

     @Test
     public void testCustomProtocolNotAllowedWithoutExplicitAddition() {
         // basicWithImages only allows http and https; cid is not listed
         Whitelist whitelist = Whitelist.basicWithImages();

         String dirty = "<img src=\"cid:12345\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         // The cid: protocol should be removed; src may be empty or missing
         assertFalse("cid: protocol should not be present without being added",
             clean.contains("cid:12345"));
     }

     @Test
     public void testStandardProtocolsStillWorkAlongsideCustom() {
         Whitelist whitelist = Whitelist.basicWithImages()
             .addProtocols("img", "src", "cid");

         String dirty = "<img src=\"http://example.com/img.png\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("http://example.com/img.png"));
     }

     @Test
     public void testProtocolWithoutColonInValueIsHandled() {
         // "cid" without trailing colon in the protocol name; the testValidProtocol
         // code appends ":" during comparison, so "cid" should match "cid:" in the src.
         Whitelist whitelist = Whitelist.none()
             .addTags("img")
             .addAttributes("img", "src")
             .addProtocols("img", "src", "cid");

         String dirty = "<img src=\"cid:test\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue("Protocol 'cid' should match 'cid:' prefix in src value",
             clean.contains("cid:test"));
     }

     @Test
     public void testNoProtocolsAddedMeansAllProtocolsRejected() {
         Whitelist whitelist = Whitelist.none()
             .addTags("img")
             .addAttributes("img", "src");
         // No addProtocols call — any URL protocol should be stripped

         String dirty = "<img src=\"http://evil.com\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertFalse("http protocol should be removed when no protocols are allowed",
             clean.contains("http://evil.com"));
     }

     @Test
     public void testCustomProtocolOnLinkHref() {
         Whitelist whitelist = Whitelist.basic()
             .addProtocols("a", "href", "custom");

         String dirty = "<a href=\"custom:page\">link</a>";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("custom:page"));
     }

     @Test
     public void testPreserveRelativeLinksInteraction() {
         // When preserveRelativeLinks is true, testValidProtocol does not update
         // attr value with absUrl, so relative URLs survive. Custom protocols
         // should still match.
         Whitelist whitelist = Whitelist.none()
             .addTags("img")
             .addAttributes("img", "src")
             .addProtocols("img", "src", "cid")
             .preserveRelativeLinks(true);

         String dirty = "<img src=\"cid:relative-test\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("cid:relative-test"));
     }

     @Test
     public void testProtocolMatchingIsCaseInsensitive() {
         Whitelist whitelist = Whitelist.none()
             .addTags("img")
             .addAttributes("img", "src")
             .addProtocols("img", "src", "cid");

         String dirty = "<img src=\"CID:UPPERCASE\" />";
         String clean = Jsoup.clean(dirty, whitelist);

         // testValidProtocol uses toLowerCase() on the value
         assertTrue("Protocol matching should be case insensitive",
             clean.contains("CID:UPPERCASE") || clean.contains("cid:UPPERCASE"));
     }

     @Test
     public void testProtocolOnEnforcedAttributeTagIsPreserved() {
         // a tag has enforced rel=nofollow in basic(); custom href protocol
         // should still be evaluated through testValidProtocol
         Whitelist whitelist = Whitelist.basic()
             .addProtocols("a", "href", "myapp");

         String dirty = "<a href=\"myapp://dashboard\">Dashboard</a>";
         String clean = Jsoup.clean(dirty, whitelist);

         assertTrue(clean.contains("myapp://dashboard"));
         assertTrue(clean.contains("rel=\"nofollow\""));
     }
 }
