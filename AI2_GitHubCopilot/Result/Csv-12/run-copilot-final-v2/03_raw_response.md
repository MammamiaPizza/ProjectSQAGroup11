package org.apache.commons.csv;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringReader;
 import java.util.Arrays;
 import java.util.List;

 import org.junit.Test;

 /**
  * Tests for {@link CSVFormat} focusing on the bug CSV-128: duplicate empty-string headers
  * are incorrectly rejected when {@code allowMissingColumnNames} is {@code true}.
  */
 public class CSVFormatBugTest {

     /**
      * Replicates the failing trigger test: header ["A","B","C","",""] (count=5)
      * with EXCEL (allowMissingColumnNames=true) plus a data line of 5 columns.
      * Must not throw IllegalArgumentException.
      */
     @Test
     public void testExcelHeaderCountLessThanData() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader("A", "B", "C", "", "");
         CSVParser parser = format.parse(new StringReader("1,2,3,4,5"));
         List<CSVRecord> records = parser.getRecords();
         assertFalse("Expected at least one record", records.isEmpty());
         CSVRecord record = records.get(0);
         assertEquals("1", record.get("A"));
         assertEquals("2", record.get("B"));
         assertEquals("3", record.get("C"));
         assertEquals("4", record.get(3)); // column 4 maps to empty string header (index 3)
         assertEquals("5", record.get(4)); // column 5 maps to second empty string header (index 4)
         parser.close();
     }

     /**
      * Duplicate non-empty headers must always throw, regardless of allowMissingColumnNames.
      * EXCEL has allowMissingColumnNames=true.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateNonEmptyHeaderWithExcel() {
         CSVFormat.EXCEL.withHeader("X", "Y", "X");
     }

     /**
      * Duplicate non-empty headers must always throw, even with RFC4180
      * (allowMissingColumnNames=false).
      */
     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateNonEmptyHeaderWithRFC4180() {
         CSVFormat.RFC4180.withHeader("X", "Y", "X");
     }

     /**
      * Header [""] (all empty, allowMissing=true) must be accepted and parsing must work.
      */
     @Test
     public void testAllEmptyHeadersWithExcel() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader("", "");
         CSVParser parser = format.parse(new StringReader("a,b"));
         CSVRecord record = parser.getRecords().get(0);
         assertEquals("a", record.get(0)); // first empty-header column accessed by index
         assertEquals("b", record.get(1)); // second empty-header column accessed by index
         parser.close();
     }

     /**
      * With allowMissingColumnNames=false (RFC4180) duplicate empty headers should throw.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testAllEmptyHeadersWithRFC4180Throws() {
         CSVFormat.RFC4180.withHeader("", "");
     }

     /**
      * Empty header array (no columns) should be valid and allow normal parsing.
      */
     @Test
     public void testEmptyHeaderArray() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader();
         CSVParser parser = format.parse(new StringReader("1,2"));
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         assertEquals("1", records.get(0).get(0));
         assertEquals("2", records.get(0).get(1));
         parser.close();
     }

     /**
      * Resetting header to null must work and produce a format without header mapping.
      */
     @Test
     public void testNullHeader() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader((String[]) null);
         assertNull(format.getHeader());
         CSVParser parser = format.parse(new StringReader("x,y"));
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         assertEquals("x", records.get(0).get(0));
         parser.close();
     }

     /**
      * Header count < column count with allowMissing=true and only non-empty headers.
      * Must not throw, extra data columns are allowed.
      */
     @Test
     public void testHeaderCountLessThanDataNoEmptyStrings() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader("A", "B");
         CSVParser parser = format.parse(new StringReader("1,2,3"));
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         CSVRecord record = records.get(0);
         assertEquals("1", record.get("A"));
         assertEquals("2", record.get("B"));
         parser.close();
     }

     /**
      * Header count > column count with allowMissing=true.
      * Must parse successfully, missing columns are simply not present.
      */
     @Test
     public void testHeaderCountMoreThanDataWithExcel() throws IOException {
         CSVFormat format = CSVFormat.EXCEL.withHeader("A", "B", "C");
         CSVParser parser = format.parse(new StringReader("1,2"));
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         CSVRecord record = records.get(0);
         assertEquals("1", record.get("A"));
         assertEquals("2", record.get("B"));
         parser.close();
     }

     /**
      * Many empty header strings with allowMissing=true must not trigger a duplicate error.
      */
     @Test
     public void testManyEmptyHeadersWithExcel() {
         String[] headers = new String[10];
         Arrays.fill(headers, "");
         CSVFormat format = CSVFormat.EXCEL.withHeader(headers);
         assertNotNull(format);
     }

     /**
      * Verify the exception message contains the duplicate name and "duplicate".
      */
     @Test
     public void testDuplicateExceptionMessage() {
         try {
             CSVFormat.EXCEL.withHeader("X", "Y", "X");
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             String msg = e.getMessage();
             assertTrue("Message should mention 'duplicate'", msg.contains("duplicate"));
             assertTrue("Message should contain the duplicate header name", msg.contains("X"));
         }
     }
 }