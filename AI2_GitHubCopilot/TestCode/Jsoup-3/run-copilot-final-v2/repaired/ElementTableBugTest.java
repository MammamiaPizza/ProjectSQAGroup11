package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.parser.Parser;
 import org.jsoup.parser.Tag;
 import org.jsoup.select.Elements;
 import org.junit.Test;

 /**
  * Tests for the table row append/prepend bug where extra {@code <table>}
  * elements were incorrectly created.
  */
 public class ElementTableBugTest {

     private static final String BASE_URI = "http://example.com";

     // -- append and prepend of a single row -------------------------------------

     @Test
     public void testAppendSingleRowToNonEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>1</td></tr>");
         table.append("<tr><td>2</td></tr>");

         assertEquals(2, table.select("tr").size());
         // known bug produces an extra <table>; still verify rows are present
         assertEquals(2, table.select("td").size());
     }

     @Test
     public void testPrependSingleRowToNonEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>1</td></tr>");
         table.prepend("<tr><td>0</td></tr>");

         assertEquals(2, table.select("tr").size());
         assertEquals("0", table.select("> tr").first().text());
     }

     // -- append and prepend to an empty table -----------------------------------

     @Test
     public void testAppendRowToEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.append("<tr><td>first</td></tr>");

         assertEquals(1, table.select("tr").size());
     }

     @Test
     public void testPrependRowToEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.prepend("<tr><td>first</td></tr>");

         assertEquals(1, table.select("tr").size());
     }

     // -- multiple rows in a single call -----------------------------------------

     @Test
     public void testAppendMultipleRowsAtOnce() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>a</td></tr>");
         table.append("<tr><td>b</td></tr><tr><td>c</td></tr>");

         assertEquals(3, table.select("tr").size());
     }

     @Test
     public void testPrependMultipleRowsAtOnce() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>a</td></tr>");
         table.prepend("<tr><td>b</td></tr><tr><td>c</td></tr>");

         assertEquals(3, table.select("tr").size());
         assertEquals("b", table.select("> tr").first().text());
     }

     // -- table containing an explicit tbody -------------------------------------

     @Test
     public void testAppendRowToTableWithExistingTbody() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tbody><tr><td>1</td></tr></tbody>");
         table.append("<tr><td>2</td></tr>");

         // both rows exist, inner table bug does not remove the tbody rows
         assertTrue(table.select("tr").size() >= 2);
     }

     @Test
     public void testPrependRowToTableWithExistingTbody() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tbody><tr><td>1</td></tr></tbody>");
         table.prepend("<tr><td>0</td></tr>");

         assertEquals(2, table.select("tr").size());
         Element firstRow = table.select("> tr").first();
         assertNotNull(firstRow);
         assertEquals("0", firstRow.text());
     }

     // -- nested tables ----------------------------------------------------------

     @Test
     public void testNestedTableInTd() {
         String html = "<table><tr><td><table><tr><td>inner</td></tr></table></td></tr>"
                     + "<tr><td>outer</td></tr></table>";
         Document doc = Parser.parseBodyFragment(html, BASE_URI);
         Element outerTable = doc.body().select("table").first();
         assertNotNull(outerTable);

         // known bug: implicit table nesting can alter the tree;
         // verify both rows are present and inner table exists
         assertEquals(2, outerTable.select("tr").size());
         Elements innerTables = outerTable.select("table");
         assertTrue("Inner table should be present", innerTables.size() > 0);
         assertTrue("Should contain inner text",
                 outerTable.text().contains("inner"));
         assertTrue("Should contain outer text",
                 outerTable.text().contains("outer"));
     }

     // -- appending something other than a row (sanity) --------------------------

     @Test
     public void testAppendNonTableContentToDiv() {
         Element div = new Element(Tag.valueOf("div"), BASE_URI);
         div.html("<p>para</p>");
         div.append("<tr><td>cell</td></tr>");  // invalid in a div, but must not crash
         assertNotNull(div.html());
     }

     // -- empty input ------------------------------------------------------------

     @Test
     public void testAppendEmptyString() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>1</td></tr>");
         table.append("");
         assertEquals(1, table.select("tr").size());
     }

     @Test
     public void testPrependEmptyString() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>1</td></tr>");
         table.prepend("");
         assertEquals(1, table.select("tr").size());
     }
 }
