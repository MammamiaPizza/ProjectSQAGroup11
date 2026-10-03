@org.junit.Test
    public void testBeforeNodeMovesSibling() {
        org.jsoup.nodes.Document doc =
org.jsoup.Jsoup.parse("<div><p>one</p><span>two</span></div>");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.jsoup.nodes.Element span = doc.select("span").first();
        span.before(p);
        org.jsoup.nodes.Node parent = span.parent();
        org.junit.Assert.assertEquals("span", parent.childNode(0).nodeName());
        org.junit.Assert.assertEquals("p", parent.childNode(1).nodeName());
    }

 @org.junit.Test
 public void testAfterNodeMovesSibling() {
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><span>two</span><p>one</p></div>");
     org.jsoup.nodes.Element p = doc.select("p").first();
     org.jsoup.nodes.Element span = doc.select("span").first();
     p.after(span);
     org.jsoup.nodes.Node parent = p.parent();
     org.junit.Assert.assertEquals("p", parent.childNode(0).nodeName());
     org.junit.Assert.assertEquals("span", parent.childNode(1).nodeName());
 }

 @org.junit.Test
 public void testAfterHtmlInsertsSibling() {
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>one</p><span>two</span></div>");
     org.jsoup.nodes.Element p = doc.select("p").first();
     p.after("<em>after</em>");
     org.jsoup.nodes.Node parent = p.parent();
     org.junit.Assert.assertEquals(3L, (long) parent.childNodes().size());
     org.junit.Assert.assertEquals("p", parent.childNode(0).nodeName());
     org.junit.Assert.assertEquals("em", parent.childNode(1).nodeName());
     org.junit.Assert.assertEquals("span", parent.childNode(2).nodeName());
 }

 @org.junit.Test
 public void testWrapEstablishesParent() {
     org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>one</p></div>");
     org.jsoup.nodes.Element p = doc.select("p").first();
     p.wrap("<div class=\"shell\"></div>");
     org.jsoup.nodes.Node parent = p.parent();
     org.junit.Assert.assertEquals("div", parent.nodeName());
     org.junit.Assert.assertEquals("shell", parent.attr("class"));
     org.junit.Assert.assertEquals("p", parent.childNode(0).nodeName());
 }