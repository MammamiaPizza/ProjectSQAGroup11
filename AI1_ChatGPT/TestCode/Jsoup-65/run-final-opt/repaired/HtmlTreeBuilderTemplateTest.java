import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class HtmlTreeBuilderTemplateTest {

    @Test
    public void templateInsideTableKeepsTemplateAndCellInsideTable() {
        Document document = Jsoup.parse("<table><template><td>Foo</td></template></table>");

        Element table = document.select("table").get(0);
        Elements templates = document.select("template");
        assertEquals(1, templates.size());

        Element template = templates.get(0);
        assertSame(table, template.parent());

        Elements cells = template.getElementsByTag("td");
        assertEquals(1, cells.size());
        assertEquals("Foo", cells.get(0).text());
    }

    @Test
    public void templateEndTagRestoresTableParsingForFollowingRow() {
        Document document = Jsoup.parse(
            "<table><template><tr><td>inside</td></tr></template><tr><td>outside</td></tr></table>");

        Element table = document.select("table").get(0);
        Element template = document.select("template").get(0);
        assertSame(table, template.parent());

        Elements rows = table.getElementsByTag("tr");
        assertEquals(2, rows.size());
        assertSame(template, rows.get(0).parent());
        assertEquals("inside", rows.get(0).getElementsByTag("td").get(0).text());
        assertEquals("outside", rows.get(1).getElementsByTag("td").get(0).text());
    }

    @Test
    public void templateInsideTableBodyRemainsInThatTableBody() {
        Document document = Jsoup.parse(
            "<table><tbody><template><tr><td>template value</td></tr></template><tr><td>row value</td></tr></tbody></table>");

        Element tbody = document.select("tbody").get(0);
        Element template = document.select("template").get(0);
        assertSame(tbody, template.parent());

        Elements templateCells = template.getElementsByTag("td");
        assertEquals(1, templateCells.size());
        assertEquals("template value", templateCells.get(0).text());

        Elements rows = tbody.getElementsByTag("tr");
        assertEquals(2, rows.size());
        assertEquals("row value", rows.get(1).getElementsByTag("td").get(0).text());
    }

    @Test
    public void unclosedTemplateInTableDoesNotMoveTemplateOutsideTable() {
        Document document = Jsoup.parse("<table><template><tr><td>unfinished</td></tr>");

        Element table = document.select("table").get(0);
        Elements templates = document.select("template");
        assertEquals(1, templates.size());

        Element template = templates.get(0);
        assertSame(table, template.parent());

        Element cell = template.getElementsByTag("td").get(0);
        assertNotNull(cell);
        assertEquals("unfinished", cell.text());
    }
}
