@org.junit.Test
public void parsesTitleContentsAsRcData() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<title>One <b>Two</b> &amp; Three</title>");

    org.junit.Assert.assertEquals("One <b>Two</b> & Three", document.title());
}

@org.junit.Test
public void preservesStyleContentsAsRawtext() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<style>one <em>two</em> &amp; three</style>");

    org.junit.Assert.assertEquals("one <em>two</em> &amp; three", document.select("style").first().data());
}

@org.junit.Test
public void ignoresWhitespaceBeforeHtmlElement() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("\t\n<html><body>body</body></html>");

    org.junit.Assert.assertEquals(1, document.childNodeSize());
    org.junit.Assert.assertEquals("body", document.body().text());
}

@org.junit.Test
public void placesNonWhitespaceBeforeHtmlElementInBody() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("prefix<html><body>body</body></html>");

    org.junit.Assert.assertEquals("prefixbody", document.body().text());
}