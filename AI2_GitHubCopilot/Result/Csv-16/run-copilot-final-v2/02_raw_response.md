package org.apache.commons.csv;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.junit.Test;

 /**
  * Tests for CSVParser iterator sequence breaking behavior (CSV-224).
  * Verifies correct record numbering and remaining record retrieval
  * after breaking out of iteration early.
  */
 public class CSVParserIteratorBreakTest {

     @Test
     public void testFullIterationNoHeader() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
         List<CSVRecord> records = parser.getRecords();
         assertEquals("Full record count", 3, records.size());
         assertEquals("First record first value", "a", records.get(0).get(0));
         assertEquals("Last record last value", "f", records.get(2).get(1));
         assertFalse("Parser not closed after getRecords", parser.isClosed());
     }

     @Test
     public void testBreakAfterFirstRecordNoHeader() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         CSVRecord first = it.next();
         assertEquals("First record value", "a", first.get(0));
         assertEquals("Record number after first", 1, parser.getRecordNumber());

         // break out, then retrieve the rest
         List<CSVRecord> remaining = parser.getRecords();
         assertEquals("Remaining records count", 2, remaining.size());
         assertEquals("First remaining value", "c", remaining.get(0).get(0));
         assertEquals("Final record number", 3, parser.getRecordNumber());
     }

     @Test
     public void testBreakAfterFirstRecordWithExplicitHeader() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader("H1", "H2");
         CSVParser parser = CSVParser.parse("v1,v2\nv3,v4\nv5,v6", format);

         // header is set explicitly – no record is consumed from input
         assertEquals("Initial record number with explicit header", 0, parser.getRecordNumber());

         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         CSVRecord first = it.next();
         assertEquals("First data value", "v1", first.get(0));
         assertEquals("Record number after first data record", 1, parser.getRecordNumber());

         List<CSVRecord> remaining = parser.getRecords();
         assertEquals("Remaining data records", 2, remaining.size());
         assertEquals("v3", remaining.get(0).get(0));
         assertEquals("Final record number after getRecords", 3, parser.getRecordNumber());

         // header map should be intact
         assertEquals(2, parser.getHeaderMap().size());
     }

     @Test
     public void testBreakAfterFirstRecordWithHeaderFromInput() throws IOException {
         // use empty header array to tell parser to read first line as header
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         CSVParser parser = CSVParser.parse("Name,Age\nJohn,30\nJane,25\nJack,40", format);

         // the header line is consumed during initialization and counts as record #1
         assertEquals("Record number after header consumption", 1, parser.getRecordNumber());

         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         CSVRecord first = it.next();
         assertEquals("First data value (by name)", "John", first.get("Name"));
         assertEquals("Record number after first data", 2, parser.getRecordNumber());

         // break; remaining data records
         List<CSVRecord> remaining = parser.getRecords();
         assertEquals("Remaining data records", 2, remaining.size());
         assertEquals("Jane", remaining.get(0).get("Name"));
         assertEquals("Final record number", 4, parser.getRecordNumber());
     }

     @Test
     public void testBreakAndContinueWithSameIterator() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it = parser.iterator();

         CSVRecord rec = it.next(); // first
         assertEquals("a", rec.get(0));
         rec = it.next(); // second
         assertEquals("c", rec.get(0));
         assertEquals("Record number after two iterations", 2, parser.getRecordNumber());

         // continue with the same iterator
         assertTrue("Has next after break", it.hasNext());
         rec = it.next();
         assertEquals("Third record", "e", rec.get(0));
         assertEquals("Record number after third", 3, parser.getRecordNumber());
         assertFalse("Iterator exhausted", it.hasNext());
     }

     @Test
     public void testBreakAndNewIteratorContinuesFromCurrent() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it1 = parser.iterator();
         it1.next(); // consume first record
         assertEquals(1, parser.getRecordNumber());

         // new iterator should start where the old one stopped
         Iterator<CSVRecord> it2 = parser.iterator();
         assertTrue(it2.hasNext());
         CSVRecord rec = it2.next();
         assertEquals("Second record via new iterator", "c", rec.get(0));
         assertEquals(2, parser.getRecordNumber());

         assertTrue(it2.hasNext());
         it2.next();
         assertEquals(3, parser.getRecordNumber());
         assertFalse(it2.hasNext());
     }

     @Test
     public void testBreakBeforeAnyNextWithExplicitHeader() throws IOException {
         // no record consumed by iterator; getRecords must return all data records
         CSVParser parser = CSVParser.parse("v1,v2\nv3,v4\nv5,v6",
                 CSVFormat.DEFAULT.withHeader("A", "B"));
         assertEquals("Record number before any iteration", 0, parser.getRecordNumber());

         List<CSVRecord> all = parser.getRecords();
         assertEquals("All data records", 3, all.size());
         assertEquals("v1", all.get(0).get(0));
         assertEquals("Final record number", 3, parser.getRecordNumber());
     }

     @Test
     public void testBreakBeforeAnyNextWithHeaderFromInput() throws IOException {
         CSVParser parser = CSVParser.parse("H1,H2\nd1,d2\nd3,d4\nd5,d6",
                 CSVFormat.DEFAULT.withHeader());
         // header line already consumed → recordNumber = 1
         assertEquals("Record number after header", 1, parser.getRecordNumber());

         // get all remaining data records
         List<CSVRecord> records = parser.getRecords();
         assertEquals("Data records count", 3, records.size());
         assertEquals("d1", records.get(0).get(0));
         assertEquals("Final record number", 4, parser.getRecordNumber());
     }

     @Test
     public void testCloseAfterPartialIteration() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         it.next(); // consume one record

         parser.close();
         assertTrue("Parser should be closed", parser.isClosed());

         // hasNext must return false when parser is closed
         assertFalse("hasNext after close", it.hasNext());

         // getRecords must throw IOException because the underlying reader is closed
         try {
             parser.getRecords();
             fail("Expected IOException after close");
         } catch (IOException expected) {
             // expected
         }
     }

     @Test
     public void testRemoveThrowsUnsupportedOperationException() throws IOException {
         CSVParser parser = CSVParser.parse("a,b", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it = parser.iterator();
         it.next();
         try {
             it.remove();
             fail("Should have thrown UnsupportedOperationException");
         } catch (UnsupportedOperationException expected) {
             // expected
         }
     }

     @Test
     public void testGetRecordsAfterFullIteratorExhaustion() throws IOException {
         CSVParser parser = CSVParser.parse("a,b\nc,d", CSVFormat.DEFAULT);
         Iterator<CSVRecord> it = parser.iterator();
         while (it.hasNext()) {
             it.next();
         }
         assertEquals("Record number after full iteration", 2, parser.getRecordNumber());
         assertTrue("getRecords after exhaustion must be empty",
                 parser.getRecords().isEmpty());
     }

     @Test
     public void testRecordNumberIncrementsCorrectly() throws IOException {
         CSVParser parser = CSVParser.parse("r1\tr2\nr3\tr4\nr5\tr6",
                 CSVFormat.TDF);
         Iterator<CSVRecord> it = parser.iterator();
         assertEquals(0, parser.getRecordNumber());
         it.next();
         assertEquals(1, parser.getRecordNumber());
         it.next();
         assertEquals(2, parser.getRecordNumber());
         it.next();
         assertEquals(3, parser.getRecordNumber());
         assertFalse(it.hasNext());
     }
 }