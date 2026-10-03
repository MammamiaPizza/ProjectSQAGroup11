package org.apache.commons.csv;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Test;

 /**
  * Tests for CSV-106: CSVPrinter.println() should handle null record separator
  * gracefully instead of appending the literal string "null".
  */
 public class CSVPrinterNullRecordSeparatorTest {

     @Test
     public void testPrintlnWithNullRecordSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("hello");
         printer.println();
         printer.print("world");
         printer.flush();
         assertEquals("helloworld", sb.toString());
     }

     @Test
     public void testPrintlnWithEmptyRecordSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("");
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("hello");
         printer.println();
         printer.print("world");
         printer.flush();
         assertEquals("helloworld", sb.toString());
     }

     @Test
     public void testPrintlnWithNormalRecordSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("hello");
         printer.println();
         printer.print("world");
         printer.flush();
         assertEquals("hello\nworld", sb.toString());
     }

     @Test
     public void testPrintRecordWithNullRecordSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.printRecord("a", "b");
         printer.printRecord("c");
         printer.flush();
         assertEquals("a,bc", sb.toString());
     }

     @Test
     public void testMultiplePrintRecordsWithNullSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.printRecord("x", "y");
         printer.printRecord("1", "2", "3");
         printer.printRecord("last");
         printer.flush();
         assertEquals("x,y1,2,3last", sb.toString());
     }

     @Test
     public void testPrintlnOnlyWithNullSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.println();
         printer.println();
         printer.println();
         printer.flush();
         assertEquals("", sb.toString());
     }

     @Test
     public void testPrintCommentWithNullRecordSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT
                 .withRecordSeparator(null)
                 .withCommentStart('#');
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.printComment("test comment");
         printer.flush();
         assertFalse("Output should not contain 'null'", sb.toString().contains("null"));
         assertTrue(sb.toString().startsWith("# test comment"));
     }

     @Test
     public void testPrintCommentMidRecordWithNullSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT
                 .withRecordSeparator(null)
                 .withCommentStart('#');
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("value");
         printer.printComment("note");
         printer.flush();
         assertFalse("Output should not contain 'null'", sb.toString().contains("null"));
         assertTrue(sb.toString().startsWith("value"));
     }

     @Test
     public void testFlushAndCloseAfterNullSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("data");
         printer.println();
         printer.flush();
         printer.close();
         assertEquals("data", sb.toString());
     }

     @Test
     public void testNewRecordStateAfterNullSeparator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         printer.print("first");
         printer.println();
         printer.print("second");
         printer.flush();
         assertEquals("firstsecond", sb.toString());
     }

     @Test
     public void testNullVersusNewlineSeparatorContrast() throws IOException {
         CSVFormat nullFormat = CSVFormat.DEFAULT.withRecordSeparator(null);
         CSVFormat newlineFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

         StringBuilder sbNull = new StringBuilder();
         CSVPrinter pNull = new CSVPrinter(sbNull, nullFormat);
         pNull.printRecord("a");
         pNull.printRecord("b");
         pNull.flush();

         StringBuilder sbNL = new StringBuilder();
         CSVPrinter pNL = new CSVPrinter(sbNL, newlineFormat);
         pNL.printRecord("a");
         pNL.printRecord("b");
         pNL.flush();

         assertEquals("ab", sbNull.toString());
         assertEquals("a\nb\n", sbNL.toString());
     }

     @Test
     public void testGetOutReturnsCorrectAppendable() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
         StringBuilder sb = new StringBuilder();
         CSVPrinter printer = new CSVPrinter(sb, format);
         assertSame(sb, printer.getOut());
     }
 }
