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
         assertEquals(0, table.select("table").size());   // no spurious <table>
     }

     @Test
     public void testPrependSingleRowToNonEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>1</td></tr>");
         table.prepend("<tr><td>0</td></tr>");

         assertEquals(2, table.select("tr").size());
         assertEquals(0, table.select("table").size());
         assertEquals("0", table.select("> tr").first().text());
     }

     // -- append and prepend to an empty table -----------------------------------

     @Test
     public void testAppendRowToEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.append("<tr><td>first</td></tr>");

         assertEquals(1, table.select("tr").size());
         assertEquals(0, table.select("table").size());
     }

     @Test
     public void testPrependRowToEmptyTable() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.prepend("<tr><td>first</td></tr>");

         assertEquals(1, table.select("tr").size());
         assertEquals(0, table.select("table").size());
     }

     // -- multiple rows in a single call -----------------------------------------

     @Test
     public void testAppendMultipleRowsAtOnce() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>a</td></tr>");
         table.append("<tr><td>b</td></tr><tr><td>c</td></tr>");

         assertEquals(3, table.select("tr").size());
         assertEquals(0, table.select("table").size());
     }

     @Test
     public void testPrependMultipleRowsAtOnce() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tr><td>a</td></tr>");
         table.prepend("<tr><td>b</td></tr><tr><td>c</td></tr>");

         assertEquals(3, table.select("tr").size());
         assertEquals(0, table.select("table").size());
         assertEquals("b", table.select("> tr").first().text());
     }

     // -- table containing an explicit tbody -------------------------------------

     @Test
     public void testAppendRowToTableWithExistingTbody() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tbody><tr><td>1</td></tr></tbody>");
         table.append("<tr><td>2</td></tr>");

         assertEquals(0, table.select("table").size()); // no extra <table>
         assertEquals(2, table.select("tr").size());      // both rows exist
     }

     @Test
     public void testPrependRowToTableWithExistingTbody() {
         Element table = new Element(Tag.valueOf("table"), BASE_URI);
         table.html("<tbody><tr><td>1</td></tr></tbody>");
         table.prepend("<tr><td>0</td></tr>");

         assertEquals(0, table.select("table").size());
         assertEquals(2, table.select("tr").size());
         // the newly prepended row should appear first
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

         // exactly one nested <table> present, and it resides inside a <td>
         assertEquals(1, outerTable.select("table").size());
         Elements innerTrs = outerTable.select("table > tr");
         assertEquals(1, innerTrs.size());
         assertEquals("inner", innerTrs.first().text());

         // the outer table still has two rows of its own
         assertEquals(2, outerTable.select("> tr").size());
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
