package org.apache.commons.csv;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.io.IOException;
 import java.io.StringWriter;
 import java.util.Arrays;
 import java.util.Collections;

 import org.junit.Test;

 /**
  * Tests for CSVPrinter header behavior. The bug (CSV-120) is that the header
  * row is not printed when the format has a header set.
  */
 public class CSVPrinterTest {

     /** Creates a printer with LF record separator and optional header. */
     private CSVPrinter createPrinter(StringWriter sw, String... header)
             throws IOException {
         CSVFormat fmt = CSVFormat.DEFAULT.withRecordSeparator("\n");
         if (header != null && header.length > 0) {
             fmt = fmt.withHeader(header);
         }
         return new CSVPrinter(sw, fmt);
     }

     /** Creates a printer with custom delimiter and record separator. */
     private CSVPrinter createPrinterCustom(StringWriter sw, char delim,
             String recSep, String... header) throws IOException {
         CSVFormat fmt = CSVFormat.newFormat(delim).withRecordSeparator(recSep);
         if (header != null && header.length > 0) {
             fmt = fmt.withHeader(header);
         }
         return new CSVPrinter(sw, fmt);
     }

     // --- Normal cases ---

     @Test
     public void testHeaderAndOneRecord() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "C1", "C2", "C3");
         printer.printRecord("V1", "V2", "V3");
         printer.close();
         assertEquals("C1,C2,C3\nV1,V2,V3\n", sw.toString());
     }

     @Test
     public void testHeaderAndMultipleRecords() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "A", "B");
         printer.printRecord("X1", "Y1");
         printer.printRecord("X2", "Y2");
         printer.close();
         assertEquals("A,B\nX1,Y1\nX2,Y2\n", sw.toString());
     }

     @Test
     public void testHeaderNotDuplicatedOnSubsequentPrints() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "H1", "H2");
         printer.printRecord("R1C1", "R1C2");
         printer.printRecord("R2C1", "R2C2");
         printer.printRecord("R3C1", "R3C2");
         printer.close();
         String out = sw.toString();
         // header must appear exactly once, at the very beginning
         assertTrue(out.startsWith("H1,H2\n"));
         // after the first newline the header should never repeat
         int firstNewline = out.indexOf('\n');
         String body = out.substring(firstNewline + 1);
         assertEquals(-1, body.indexOf("H1,H2"));
     }

     @Test
     public void testNoHeaderWhenNoHeaderSet() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw /* no header */);
         printer.printRecord("D1", "D2");
         printer.close();
         assertEquals("D1,D2\n", sw.toString());
     }

     @Test
     public void testHeaderOnlyOnFirstRecordCall() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "T1", "T2");
         // close without calling printRecord – no header should appear
         printer.close();
         assertEquals("", sw.toString());
     }

     // --- Boundary cases ---

     @Test
     public void testHeaderWithNullColumn() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "Col1", null, "Col3");
         printer.printRecord("a", "b", "c");
         printer.close();
         // null column prints empty field (default nullString is empty)
         assertEquals("Col1,,Col3\na,b,c\n", sw.toString());
     }

     @Test
     public void testHeaderAndRecordWithNullValues() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "P", "Q");
         printer.printRecord("val1", null);
         printer.close();
         assertEquals("P,Q\nval1,\n", sw.toString());
     }

     @Test
     public void testHeaderAndEmptyRecordVarargs() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "H");
         // printRecord with no arguments prints an empty record (just separator)
         printer.printRecord();
         printer.close();
         // header row then empty row
         assertEquals("H\n\n", sw.toString());
     }

     @Test
     public void testHeaderAndEmptyRecordIterable() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "K");
         // empty iterable still triggers header, then empty record line
         printer.printRecord(Collections.emptyList());
         printer.close();
         assertEquals("K\n\n", sw.toString());
     }

     @Test
     public void testPrintRecordsWithHeader() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "Name", "Age");
         printer.printRecords(Arrays.asList(
                 new Object[]{"Alice", "30"},
                 new Object[]{"Bob", "25"}));
         printer.close();
         assertEquals("Name,Age\nAlice,30\nBob,25\n", sw.toString());
     }

     @Test
     public void testCustomDelimiterAndRecordSeparator() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinterCustom(sw, ';', "\r\n",
                 "X", "Y", "Z");
         printer.printRecord("1", "2", "3");
         printer.close();
         assertEquals("X;Y;Z\r\n1;2;3\r\n", sw.toString());
     }

     @Test
     public void testHeaderOutputHasNoTrailingDelimiter() throws IOException {
         StringWriter sw = new StringWriter();
         CSVPrinter printer = createPrinter(sw, "A", "B", "C");
         printer.printRecord("1", "2", "3");
         printer.close();
         String out = sw.toString();
         // header row must end with recordSeparator, not with delimiter
         assertTrue("Header row should not end with delimiter",
                 out.startsWith("A,B,C\n"));
     }
 }
