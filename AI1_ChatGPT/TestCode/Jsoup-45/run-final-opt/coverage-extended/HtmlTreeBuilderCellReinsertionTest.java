package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HtmlTreeBuilderCellReinsertionTest {

    @Test
    public void parsesSingleHeaderCellWithoutDuplicatingIt() {
        Document document = Jsoup.parse("<table><tr><th>Header</th></tr></table>");

        Elements headers = document.select("th");
        assertEquals(1, headers.size());
        assertEquals("Header", headers.get(0).text());
        assertEquals("tr", headers.get(0).parent().tagName());
    }

    @Test
    public void closesHeaderCellBeforeProcessingFollowingDataCell() {
        Document document = Jsoup.parse("<table><tr><th>Heading<td>Value</tr></table>");

        Elements headers = document.select("tr > th");
        Elements cells = document.select("tr > td");

        assertEquals(1, headers.size());
        assertEquals("Heading", headers.get(0).text());
        assertEquals(1, cells.size());
        assertEquals("Value", cells.get(0).text());
    }

    @Test
    public void handlesAdjacentHeaderCellsWithOmittedEndTags() {
        Document document = Jsoup.parse("<table><tr><th>One<th>Two<td>Three</tr></table>");

        Elements headers = document.select("tr > th");
        Elements cells = document.select("tr > td");

        assertEquals(2, headers.size());
        assertEquals("One", headers.get(0).text());
        assertEquals("Two", headers.get(1).text());
        assertEquals(1, cells.size());
        assertEquals("Three", cells.get(0).text());
    }

    @Test
    public void returnsToRowModeAfterClosingHeaderCell() {
        Document document = Jsoup.parse("<table><tr><th>First</th><th>Second</th><td>Third</td></tr></table>");

        Element row = document.select("tr").first();
        assertNotNull(row);
        assertEquals(3, row.children().size());
        assertEquals("th", row.child(0).tagName());
        assertEquals("th", row.child(1).tagName());
        assertEquals("td", row.child(2).tagName());
        assertEquals("First", row.child(0).text());
        assertEquals("Second", row.child(1).text());
        assertEquals("Third", row.child(2).text());
    }

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
}
