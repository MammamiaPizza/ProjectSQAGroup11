@Test
public void decodesNamedAndNumericCharacterReferencesInText() {
    org.junit.Assert.assertEquals("&<>\"AB",
        org.jsoup.Jsoup.parse("<p>&amp;&lt;&gt;&quot;&#65;&#x42;</p>").select("p").text());
}

@Test
public void decodesCharacterReferencesInAttributesWithoutConsumingLiteralAmpersands() {
    org.junit.Assert.assertEquals("a&b",
        org.jsoup.Jsoup.parse("<a title='a&amp;b' href='?x=1&y=2'>link</a>").select("a").attr("title"));
    org.junit.Assert.assertEquals("?x=1&y=2",
        org.jsoup.Jsoup.parse("<a title='a&amp;b' href='?x=1&y=2'>link</a>").select("a").attr("href"));
}

@Test
public void parsesSelfClosingStartTagsWithoutAffectingFollowingContent() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<div><img src='x'/><p>after</p></div>");

    org.junit.Assert.assertEquals(1, document.select("img").size());
    org.junit.Assert.assertEquals("after", document.select("p").text());
}