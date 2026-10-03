import static org.junit.Assert.*;
 import org.junit.Test;
 import org.apache.commons.csv.*;
 import java.io.*;
 import java.nio.charset.Charset;
 import java.util.*;

 public class CSVParserTest {

     private CSVParser parseWithHeader(String csvData, CSVFormat format) throws IOException {
         return CSVParser.parse(csvData, format);
     }

     @Test
     public void testHeaderMissingWithNull() throws IOException {
         // Bug CSV-122: A header value matching nullString (becoming null) should not cause NPE
         CSVFormat format = CSVFormat.DEFAULT.withHeader().withNullString("NULL");
         CSVParser parser = CSVParser.parse("a,NULL,c", format);
         assertNotNull("Parser should be created", parser);
         Map<String, Integer> headerMap = parser.getHeaderMap();
         assertNotNull("Header map should not be null", headerMap);
         assertTrue("Header map must contain null key", headerMap.containsKey(null));
     }

     @Test
     public void testAddRecordValueWithNullString() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
         CSVParser parser = CSVParser.parse("hello,NULL,world", format);
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         CSVRecord record = records.get(0);
         assertEquals("hello", record.get(0));
         assertNull(record.get(1));
         assertEquals("world", record.get(2));
     }

     @Test
     public void testAddRecordValueWithoutNullString() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT;
         CSVParser parser = CSVParser.parse("hello,NULL,world", format);
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         assertEquals("NULL", records.get(0).get(1));
     }

     @Test
     public void testAddRecordValueNullStringCaseInsensitive() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
         CSVParser parser = CSVParser.parse("a,null,b", format);
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         assertNull(records.get(0).get(1));
     }

     @Test
     public void testEmptyHeader() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         CSVParser parser = CSVParser.parse(",b,c\n1,2,3", format);
         Map<String, Integer> headerMap = parser.getHeaderMap();
         assertNotNull(headerMap);
         assertTrue(headerMap.containsKey(""));
         assertEquals(Integer.valueOf(0), headerMap.get(""));
     }

     @Test
     public void testDuplicateHeaderThrows() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         try {
             CSVParser.parse("a,b,a\n1,2,3", format);
             fail("Expected IllegalArgumentException for duplicate header");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }

     @Test
     public void testAllValuesNulling() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader().withNullString("NULL");
         CSVParser parser = CSVParser.parse("X,Y\nNULL,NULL", format);
         List<CSVRecord> records = parser.getRecords();
         assertEquals(1, records.size());
         CSVRecord record = records.get(0);
         assertNull(record.get(0));
         assertNull(record.get(1));
     }

     @Test
     public void testGetHeaderMapNullWhenNoHeader() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT;
         CSVParser parser = CSVParser.parse("a,b,c", format);
         assertNull(parser.getHeaderMap());
     }

     @Test
     public void testRecordSizeMatchesHeaderCount() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         CSVParser parser = CSVParser.parse("X,Y,Z\n1,2,3\n4,5,6", format);
         Map<String, Integer> headerMap = parser.getHeaderMap();
         assertEquals(3, headerMap.size());
         List<CSVRecord> records = parser.getRecords();
         for (CSVRecord record : records) {
             assertEquals(headerMap.size(), record.size());
         }
     }

     @Test
     public void testIterator() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         CSVParser parser = CSVParser.parse("A,B\n1,2\n3,4", format);
         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         assertEquals("1", it.next().get("A"));
         assertTrue(it.hasNext());
         assertEquals("4", it.next().get("B"));
         assertFalse(it.hasNext());
     }

     @Test
     public void testGetHeaderMapCopy() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader();
         CSVParser parser = CSVParser.parse("K,L\n1,2", format);
         Map<String, Integer> map1 = parser.getHeaderMap();
         Map<String, Integer> map2 = parser.getHeaderMap();
         map2.put("new", -1);
         assertFalse(map1.containsKey("new"));
     }

     @Test
     public void testRecordNumberIncrement() throws IOException {
         CSVFormat format = CSVFormat.DEFAULT.withHeader("Name");
         CSVParser parser = CSVParser.parse("value1\nvalue2", format);
         Iterator<CSVRecord> it = parser.iterator();
         assertTrue(it.hasNext());
         assertEquals(1L, it.next().getRecordNumber());
         assertTrue(it.hasNext());
         assertEquals(2L, it.next().getRecordNumber());
         assertFalse(it.hasNext());
     }
 }