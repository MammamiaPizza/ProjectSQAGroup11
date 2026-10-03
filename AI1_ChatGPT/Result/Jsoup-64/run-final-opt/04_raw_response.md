@Test
public void titleRcDataAllowsWhitespaceAndFollowingHeadContent() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<html><head>\n\t<title>A &amp; B <b>C</b></title><meta name=foo></head><body>One</body></html>");

    org.junit.Assert.assertEquals("A & B <b>C</b>", document.title());
    org.junit.Assert.assertEquals(1, document.head().getElementsByTag("meta").size());
    org.junit.Assert.assertEquals("foo", document.head().getElementsByTag("meta").first().attr("name"));
    org.junit.Assert.assertEquals("One", document.body().text());
}

@Test
public void textareaRcDataTreatsMarkupAsTextAndResumesBodyParsing() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<textarea><b>one</b>&amp;two</textarea><p>after</p>");

    org.junit.Assert.assertEquals("<b>one</b>&two", document.body().getElementsByTag("textarea").first().text());
    org.junit.Assert.assertEquals("after", document.body().getElementsByTag("p").first().text());
}