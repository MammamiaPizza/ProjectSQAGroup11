package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class HtmlTreeBuilderTemplateTest {

     // Basic test: template as direct child of <table>
     @Test
     public void testTemplateInsideTable() {
         String html = "<table><template><tr><td>Hello</td></tr></template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         assertNotNull("Table should exist", table);
         Elements templates = table.children().select("template");
         assertFalse("Template should be inside table", templates.isEmpty());
         Element template = templates.first();
         assertEquals("Template content should have one child", 1, template.childNodes().size());
         assertTrue("Table should not directly contain tr/td",
table.children().select("tr").isEmpty());
     }

     // Template inside <tbody> (InTableBody state)
     @Test
     public void testTemplateInsideTbody() {
         String html = "<table><tbody><template><tr><td>Cell</td></tr></template></tbody></table>";
         Document doc = Jsoup.parse(html);
         Element tbody = doc.select("tbody").first();
         assertNotNull("Tbody should exist", tbody);
         Elements templates = tbody.children().select("template");
         assertFalse("Template should be inside tbody", templates.isEmpty());
         assertFalse("Template content should not be empty",
templates.first().childNodes().isEmpty());
     }

     // Template inside <tr> (InRow state)
     @Test
     public void testTemplateInsideTr() {
         String html = "<table><tr><template><td>Inside TR</td></template></tr></table>";
         Document doc = Jsoup.parse(html);
         Element tr = doc.select("tr").first();
         assertNotNull("Tr should exist", tr);
         Elements templates = tr.children().select("template");
         assertFalse("Template should be inside tr", templates.isEmpty());
     }

     // Template inside <td> (InCell state)
     @Test
     public void testTemplateInsideTd() {
         String html = "<table><tr><td><template><span>Inner</span></template></td></tr></table>";
         Document doc = Jsoup.parse(html);
         Element td = doc.select("td").first();
         assertNotNull("Td should exist", td);
         Elements templates = td.children().select("template");
         assertFalse("Template should be inside td", templates.isEmpty());
         Element template = templates.first();
         assertTrue("Template should contain parsed children", template.childNodes().size() > 0);
     }

     // Empty template inside table - should still be present
     @Test
     public void testEmptyTemplateInsideTable() {
         String html = "<table><template></template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements templates = table.children().select("template");
         assertEquals("Exactly one template expected", 1, templates.size());
         assertTrue("Template content should be empty", templates.first().childNodes().isEmpty());
     }

     // Multiple templates inside a single table
     @Test
     public void testMultipleTemplatesInsideTable() {
         String html = "<table><template>First</template><template>Second</template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements templates = table.children().select("template");
         assertEquals("Two templates expected", 2, templates.size());
         assertEquals("First", templates.get(0).childNode(0).outerHtml().trim());
         assertEquals("Second", templates.get(1).childNode(0).outerHtml().trim());
     }

     // Template after a <tr> inside <table> should remain a table child
     @Test
     public void testTemplateAfterRowInsideTable() {
         String html =
"<table><tr><td>Row1</td></tr><template><tr><td>Row2</td></tr></template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements children = table.children();
         // children should be exactly [tr, template]
         assertEquals("Table should have 2 children", 2, children.size());
         assertEquals("First child should be tr", "tr", children.get(0).tagName());
         assertEquals("Second child should be template", "template", children.get(1).tagName());
     }

     // Unclosed template at EOF – parser should still place it inside table
     @Test
     public void testUnclosedTemplateAtEofInsideTable() {
         String html = "<table><template>unclosed";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         assertNotNull("Table should exist", table);
         Elements templates = table.children().select("template");
         assertFalse("Template should be inside table even when unclosed", templates.isEmpty());
         assertTrue("Template should contain text content", templates.first().childNodes().size() >=
1);
     }

     // Template along with other table section elements (caption, tfoot)
     @Test
     public void testTemplateInsideTableWithOtherTableElements() {
         String html = "<table><caption>Cap</caption><template><tr><td>Test</td></tr></template><tfo
ot><tr><td>Foot</td></tr></tfoot></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements templates = table.children().select("template");
         assertFalse("Template should be inside table alongside other children",
templates.isEmpty());
         Element template = templates.first();
         assertTrue("Template content should not be empty", template.childNodes().size() > 0);
     }

     // Nested templates inside table – outer should be table child, inner its content
     @Test
     public void testNestedTemplatesInsideTable() {
         String html = "<table><template><template>Nested</template></template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements templates = table.children().select("template");
         assertEquals("Only outer template should be direct table child", 1, templates.size());
         Element outer = templates.first();
         assertEquals("Outer template content should have one child", 1, outer.childNodes().size());
         Element inner = outer.child(0);
         assertEquals("Inner child should be template", "template", inner.tagName());
         assertEquals("Nested text", "Nested", inner.childNode(0).outerHtml().trim());
     }

     // Whitespace-only content in template – still inserted in table
     @Test
     public void testTemplateWithWhitespaceContent() {
         String html = "<table><template>  \n  </template></table>";
         Document doc = Jsoup.parse(html);
         Element table = doc.select("table").first();
         Elements templates = table.children().select("template");
         assertFalse("Template with whitespace should still be inside table", templates.isEmpty());
     }

     // Ensure template outside table is not erroneously moved inside it
     @Test
     public void testTemplateOutsideTableNotAffected() {
         String html = "<template><div>Outside</div></template><table></table>";
         Document doc = Jsoup.parse(html);
         Element body = doc.body();
         Elements templates = body.children().select("template");
         assertEquals("Template outside table should appear in body", 1, templates.size());
         Element template = templates.first();
         assertEquals("Template should have one child", 1, template.childNodes().size());
     }
 }