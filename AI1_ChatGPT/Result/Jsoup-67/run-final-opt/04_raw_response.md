@Test
public void closesImpliedTableCellsBeforeStartingTheNextCell() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<table><tbody><tr><td>one<td>two</tr></tbody></table><p>after</p>");

    org.junit.Assert.assertEquals(2, document.select("td").size());
    org.junit.Assert.assertEquals("one", document.select("td").get(0).text());
    org.junit.Assert.assertEquals("two", document.select("td").get(1).text());
    org.junit.Assert.assertEquals("tr", document.select("td").get(0).parent().tagName());
    org.junit.Assert.assertEquals("after", document.select("p").first().text());
}

@Test
public void reconstructsActiveFormattingElementsAfterMisnestedFormattingEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<p><b>one<i>two</b>three</i>four</p>");

    org.jsoup.nodes.Element paragraph = document.select("p").first();
    org.junit.Assert.assertEquals("one two three four", paragraph.text());
    org.junit.Assert.assertEquals(1, paragraph.select("b").size());
    org.junit.Assert.assertEquals("one two", paragraph.select("b").first().text());
    org.junit.Assert.assertEquals(2, paragraph.select("i").size());
    org.junit.Assert.assertEquals("three", paragraph.select("p > i").first().text());
}

@Test
public void closesPreviousOptionsAndOptgroupsWhenNewOptionsAreStarted() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<select><option>one<option>two<optgroup label='group'><option>three</select>");

    org.junit.Assert.assertEquals(3, document.select("option").size());
    org.junit.Assert.assertEquals("one", document.select("option").get(0).text());
    org.junit.Assert.assertEquals("two", document.select("option").get(1).text());
    org.junit.Assert.assertEquals("three", document.select("option").get(2).text());
    org.junit.Assert.assertEquals("optgroup", document.select("option").get(2).parent().tagName());
}