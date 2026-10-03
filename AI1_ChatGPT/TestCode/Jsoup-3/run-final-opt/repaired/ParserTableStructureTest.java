package org.jsoup.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ParserTableStructureTest {

    @Test
    public void appendRowToTableAddsRowAsTableChild() {
        Element table = tableWithOneRow();

        table.append("<tr><td>2</td></tr>");

        assertEquals("<table><tr><td>1</td></tr><tr><td>2</td></tr></table>", table.toString());
    }

    @Test
    public void prependRowToTableAddsRowBeforeExistingRow() {
        Element table = tableWithOneRow();

        table.prepend("<tr><td>2</td></tr>");

        assertEquals("<table><tr><td>2</td></tr><tr><td>1</td></tr></table>", table.toString());
    }

    @Test
    public void nestedImplicitTablesKeepTheirOwnImplicitRows() {
        Document document = Parser.parse(
                "<table><td><table><td>3</td><td>4</td></table><td>5</td></table>", "");

        assertEquals(
                "<table><tr><td><table><tr><td>3</td><td>4</td></tr></table></td></tr><tr><td>5</td></tr></table>",
                document.body().html());
    }

    @Test
    public void appendingEmptyHtmlLeavesTableUnchangedAndReturnsTable() {
        Element table = tableWithOneRow();

        Element result = table.append("");

        assertSame(table, result);
        assertEquals("<table><tr><td>1</td></tr></table>", table.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendRejectsNullHtml() {
        tableWithOneRow().append(null);
    }

    @Test
    public void predefinedTagsAreCaseInsensitiveAndCanonical() {
        Tag lowerCase = Tag.valueOf("tr");
        Tag upperCase = Tag.valueOf("TR");

        assertSame(lowerCase, upperCase);
        assertEquals("tr", upperCase.getName());
    }

    private Element tableWithOneRow() {
        Element table = new Element(Tag.valueOf("table"), "");
        table.appendElement("tr").appendElement("td").appendText("1");
        return table;
    }
}
