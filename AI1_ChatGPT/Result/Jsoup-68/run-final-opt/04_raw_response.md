@org.junit.Test
public void closesCellsAndRowsWhenANewRowStarts() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<table><tbody><tr><td>one<tr><td>two</table>");

    org.junit.Assert.assertEquals("one two", document.text());
    org.junit.Assert.assertEquals(2, document.getElementsByTag("tr").size());
    org.junit.Assert.assertEquals(2, document.getElementsByTag("td").size());
}

@org.junit.Test
public void closesTableBodyBeforeStartingAnotherBody() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<table><tbody><tr><td>one<tbody><tr><td>two</table>");

    org.junit.Assert.assertEquals("one two", document.text());
    org.junit.Assert.assertEquals(2, document.getElementsByTag("tbody").size());
    org.junit.Assert.assertEquals(2, document.getElementsByTag("tr").size());
}

@org.junit.Test
public void clearsFormattingElementsWhenEnteringTableRows() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<table><b>one<tr><td>two</table>");

    org.junit.Assert.assertEquals("one two", document.text());
    org.junit.Assert.assertEquals(1, document.getElementsByTag("b").size());
    org.junit.Assert.assertEquals(0, document.getElementsByTag("table").first().getElementsByTag("b").size());
}

@org.junit.Test
public void handlesMisnestedFormattingAroundABlockElement() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<b><p>one</b>two</p>");

    org.junit.Assert.assertEquals("one two", document.text());
    org.junit.Assert.assertEquals(1, document.getElementsByTag("p").size());
}