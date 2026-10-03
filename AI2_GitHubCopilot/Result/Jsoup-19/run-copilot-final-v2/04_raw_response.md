@Test
    public void testSimpleTextCoverage() {
        Whitelist wl = Whitelist.simpleText();
        String clean = Jsoup.clean("<b>Bold</b> <em>Italic</em> <p>Paragraph</p>", wl);
        assertTrue(clean.contains("<b>Bold</b>"));
        assertTrue(clean.contains("<em>Italic</em>"));
        assertFalse(clean.contains("<p>"));
    }

 @Test
 public void testAddAttributesToExistingTag() {
     Whitelist wl = Whitelist.basic();
     wl.addAttributes("a", "target");
     String clean = Jsoup.clean("<a href=\"http://example.com\" target=\"_blank\">link</a>", wl);
     assertTrue(clean.contains("href"));
     assertTrue(clean.contains("target=\"_blank\""));
 }

 @Test
 public void testAddMultipleEnforcedAttributesForSameTag() {
     Whitelist wl = Whitelist.basic();
     wl.addEnforcedAttribute("a", "rel", "nofollow");
     wl.addEnforcedAttribute("a", "target", "_blank");
     String clean = Jsoup.clean("<a href=\"http://example.com\">link</a>", wl);
     assertTrue(clean.contains("rel=\"nofollow\""));
     assertTrue(clean.contains("target=\"_blank\""));
 }

 @Test
 public void testAttributeRemovedWhenNotInAllowedAttributesForTag() {
     Whitelist wl = Whitelist.none();
     wl.addTags("div");
     wl.addAttributes("div", "id");
     String clean = Jsoup.clean("<div class=\"foo\">text</div>", wl);
     assertTrue(clean.contains("<div>text</div>"));
     assertFalse(clean.contains("class"));
 }