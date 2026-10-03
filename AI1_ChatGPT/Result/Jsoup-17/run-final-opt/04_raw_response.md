@Test
public void parsesTextareaContentsAsRcdata() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<textarea>a<b>&amp;c</textarea>");
    assertEquals("a<b>&c", document.select("textarea").text());
    assertEquals(0, document.select("textarea b").size());
}

@Test
public void parsesStyleContentsAsRawtext() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<style>a<b>c</style>");
    assertEquals("a<b>c", document.select("style").text());
    assertEquals(0, document.select("style b").size());
}