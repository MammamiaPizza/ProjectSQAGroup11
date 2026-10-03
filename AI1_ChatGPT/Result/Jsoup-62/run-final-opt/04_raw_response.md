@Test
public void parsesTextareaContentsAsRcdata() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<textarea>One &amp; <b>Two</b></textarea><p>after</p>");

    org.jsoup.nodes.Element textarea = document.select("textarea").first();
    org.junit.Assert.assertEquals("One & <b>Two</b>", textarea.text());
    org.junit.Assert.assertEquals(0, textarea.children().size());
    org.junit.Assert.assertEquals("after", document.select("p").first().text());
}

@Test
public void parsesStyleContentsAsRawtext() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<style>.x { content: '<b>&amp;'; }</style><p>after</p>");

    org.jsoup.nodes.Element style = document.select("style").first();
    org.junit.Assert.assertEquals(".x { content: '<b>&amp;'; }", style.data());
    org.junit.Assert.assertEquals(0, style.children().size());
    org.junit.Assert.assertEquals("after", document.select("p").first().text());
}

@Test
public void handlesWhitespaceAndTextDifferentlyInsideTables() {
    org.jsoup.nodes.Document whitespaceDocument = org.jsoup.Jsoup.parse("<table> \n<tr><td>A</td></tr></table>");
    org.jsoup.nodes.Element table = whitespaceDocument.select("table").first();

    org.junit.Assert.assertTrue(table.childNode(0) instanceof org.jsoup.nodes.TextNode);
    org.junit.Assert.assertEquals(" \n", ((org.jsoup.nodes.TextNode) table.childNode(0)).getWholeText());

    org.jsoup.nodes.Document textDocument = org.jsoup.Jsoup.parse("<table>text<tr><td>A</td></tr></table>");
    org.jsoup.nodes.Element body = textDocument.body();

    org.junit.Assert.assertTrue(body.childNode(0) instanceof org.jsoup.nodes.TextNode);
    org.junit.Assert.assertEquals("text", ((org.jsoup.nodes.TextNode) body.childNode(0)).getWholeText());
}