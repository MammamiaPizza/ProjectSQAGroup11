@Test
 public void testAdoptionAgencyAboveOnStack() {
     String html = "<p><b><i>text</b> after</i></p>";
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
     org.junit.Assert.assertNotNull(doc);
     org.junit.Assert.assertTrue("Expected at least one <i> element", doc.select("i").size() > 0);
 }

 @Test
 public void testClearStackToContextCoverage() {
     String html = "<table><tr><td>text</tr></table>";
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
     org.junit.Assert.assertEquals("text", doc.select("td").text());
 }

 @Test
 public void testGenerateImpliedEndTagsCoverage() {
     String html = "<table><tr><td><p>text</td></tr></table>";
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
     org.junit.Assert.assertTrue(doc.select("p").size() > 0);
     org.junit.Assert.assertEquals("text", doc.select("td").text());
 }

 @Test
 public void testMisnestedFormattingInTableCell() {
     String html = "<table><tr><td><b><i>text</b> more</i></td></tr></table>";
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
     org.junit.Assert.assertEquals("Expected exactly one <b>", 1, doc.select("b").size());
     org.junit.Assert.assertEquals("Expected exactly one <i>", 1, doc.select("i").size());
 }