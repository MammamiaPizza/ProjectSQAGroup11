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
}