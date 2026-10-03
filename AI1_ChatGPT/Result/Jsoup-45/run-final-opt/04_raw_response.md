@Test
public void closesHeaderCellWhenRowEndTagIsEncountered() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<table><tr><th>Heading</tr><tr><td>Value</td></tr></table>");

    junit.framework.Assert.assertEquals(1, document.select("th").size());
    junit.framework.Assert.assertEquals("Heading", document.select("th").get(0).text());
    junit.framework.Assert.assertEquals(1, document.select("td").size());
    junit.framework.Assert.assertEquals("Value", document.select("td").get(0).text());
}

@Test
public void preservesNestedFormattingWhenClosingAnImplicitHeaderCell() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<table><tr><th><b>First</b><th>Second</tr></table>");

    junit.framework.Assert.assertEquals(2, document.select("th").size());
    junit.framework.Assert.assertEquals("First", document.select("th").get(0).text());
    junit.framework.Assert.assertEquals("Second", document.select("th").get(1).text());
    junit.framework.Assert.assertEquals(1, document.select("th b").size());
}

@Test
public void parsesHeaderCellsInsideTableSectionsWithoutDuplication() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<table><thead><tr><th>Heading</th></tr></thead>"
            + "<tbody><tr><td>Value</td></tr></tbody></table>");

    junit.framework.Assert.assertEquals(1, document.select("th").size());
    junit.framework.Assert.assertEquals("Heading", document.select("th").get(0).text());
    junit.framework.Assert.assertEquals(1, document.select("td").size());
    junit.framework.Assert.assertEquals("Value", document.select("td").get(0).text());
}