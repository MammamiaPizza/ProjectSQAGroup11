package org.apache.commons.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**

 - Tests for {@link CSVParser} focusing on the bug where {@code getHeaderMap()}
 - throws a NullPointerException when no header is defined.
  */
 public class CSVParserTest {
  @Test
  public void testGetHeaderMapWithoutHeader() throws IOException {
  // When no header is defined, getHeaderMap() should return null (or empty)
  // without throwing an exception. Buggy version throws NPE.
  CSVParser parser = CSVParser.parse("a,b,c", CSVFormat.DEFAULT);
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNull("Header map should be null when no header defined", headerMap);
  parser.close();
  }
  @Test
  public void testGetHeaderMapWithHeader() throws IOException {
  CSVFormat format = CSVFormat.DEFAULT.withHeader("name", "age");
  CSVParser parser = CSVParser.parse("John,30\nJane,25", format);
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNotNull("Header map must not be null when header is defined", headerMap);
  assertEquals("Unexpected map size", 2, headerMap.size());
  assertEquals("Wrong index for 'name'", Integer.valueOf(0), headerMap.get("name"));
  assertEquals("Wrong index for 'age'", Integer.valueOf(1), headerMap.get("age"));
  parser.close();
  }
  @Test
  public void testGetHeaderMapAfterReadingRecordsWithoutHeader() throws IOException {
  CSVParser parser = CSVParser.parse("x,y\n1,2", CSVFormat.DEFAULT);
  // Consume all records
  List<CSVRecord> records = parser.getRecords();
  assertFalse("Records should be present", records.isEmpty());
  // getHeaderMap() must not throw NPE after reading records
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNull(headerMap);
  parser.close();
  }
  @Test
  public void testGetHeaderMapBeforeReadingRecordWithHeader() throws IOException {
  CSVFormat format = CSVFormat.DEFAULT.withHeader("colA", "colB");
  CSVParser parser = CSVParser.parse("v1,v2\nv3,v4", format);
  // Header map should be available immediately after construction
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNotNull(headerMap);
  assertEquals(2, headerMap.size());
  assertEquals(Integer.valueOf(0), headerMap.get("colA"));
  parser.close();
  }
  @Test
  public void testGetHeaderMapWithEmptyHeaderLine() throws IOException {
  // When header array is empty, first record is interpreted as header
  CSVFormat format = CSVFormat.DEFAULT.withHeader();
  CSVParser parser = CSVParser.parse("h1,h2\n1,2", format);
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNotNull(headerMap);
  assertEquals(2, headerMap.size());
  assertEquals(Integer.valueOf(0), headerMap.get("h1"));
  assertEquals(Integer.valueOf(1), headerMap.get("h2"));
  parser.close();
  }
  @Test
  public void testParseAndIterateWithoutHeader() throws IOException {
  CSVParser parser = CSVParser.parse("1,2,3\n4,5,6", CSVFormat.DEFAULT);
  int count = 0;
  for (CSVRecord record : parser) {
      assertNotNull(record);
      count++;
  }
  assertEquals("Should iterate over 2 records", 2, count);
  parser.close();
  }
  @Test
  public void testGetRecordsWithoutHeader() throws IOException {
  CSVParser parser = CSVParser.parse("x,y\nz,w", CSVFormat.DEFAULT);
  List<CSVRecord> records = parser.getRecords();
  assertEquals(2, records.size());
  assertEquals("x", records.get(0).get(0));
  assertEquals("y", records.get(0).get(1));
  assertEquals("z", records.get(1).get(0));
  assertEquals("w", records.get(1).get(1));
  parser.close();
  }
  @Test
  public void testGetHeaderMapReturnsNewMapOnEachCall() throws IOException {
  CSVFormat format = CSVFormat.DEFAULT.withHeader("k");
  CSVParser parser = CSVParser.parse("v", format);
  Map<String, Integer> map1 = parser.getHeaderMap();
  Map<String, Integer> map2 = parser.getHeaderMap();
  assertNotSame("Each call should return a new map", map1, map2);
  assertEquals(map1, map2);
  parser.close();
  }
  @Test
  public void testCloseAndCheckClosed() throws IOException {
  CSVParser parser = CSVParser.parse("a", CSVFormat.DEFAULT);
  assertFalse(parser.isClosed());
  parser.close();
  assertTrue(parser.isClosed());
  }
  @Test
  public void testLineNumberAndRecordNumber() throws IOException {
  CSVFormat format = CSVFormat.DEFAULT.withHeader("h1", "h2");
  // Header line is consumed/skipped depending on config, but here skipHeaderRecord
  // is false by default for withHeader. We just check counters.
  CSVParser parser = CSVParser.parse("r1c1,r1c2\nr2c1,r2c2", format);
  // read first record
  assertTrue(parser.iterator().hasNext());
  CSVRecord rec1 = parser.iterator().next();
  assertEquals(1, parser.getRecordNumber());
  // line number may vary; we simply verify it's > 0
  assertTrue(parser.getCurrentLineNumber() > 0);
  parser.close();
  }
  @Test
  public void testGetHeaderMapWithSkipHeaderRecord() throws IOException {
  CSVFormat format = CSVFormat.DEFAULT.withHeader("A", "B").withSkipHeaderRecord(true);
  CSVParser parser = CSVParser.parse("A,B\n1,2\n3,4", format);
  Map<String, Integer> headerMap = parser.getHeaderMap();
  assertNotNull(headerMap);
  assertEquals(2, headerMap.size());
  // First data record should be "1,2", not "A,B"
  CSVRecord firstRecord = parser.iterator().next();
  assertEquals("1", firstRecord.get(0));
  assertEquals("2", firstRecord.get(1));
  parser.close();
  }
  @Test
  public void testNoHeaderMapDoesNotAffectRecordAccess() throws IOException {
  CSVParser parser = CSVParser.parse("val1,val2", CSVFormat.DEFAULT);
  List<CSVRecord> records = parser.getRecords();
  assertEquals(1, records.size());
  CSVRecord record = records.get(0);
  assertEquals("val1", record.get(0));
  assertEquals("val2", record.get(1));
  // record.get("some") should perhaps work? With null header map it might fail.
  // But just verify numeric access works.
  parser.close();
  }

}