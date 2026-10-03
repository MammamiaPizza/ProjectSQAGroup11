package org.apache.commons.csv;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

/**

 - JUnit tests for CSVRecord focusing on toMap() NPE bug (CSV-118)
 - and related mapping-based accessors.
  */
 public class CSVRecordTest {
  // --- helper to build a record ---
  private static CSVRecord record(String[] values, Map<String, Integer> mapping,
                               String comment, long recNum) {
  return new CSVRecord(values, mapping, comment, recNum);
  }
  private static CSVRecord record(String[] values, Map<String, Integer> mapping) {
  return record(values, mapping, null, 1L);
  }
  // ======================= toMap tests =======================
  @Test
  public void testToMapWithNoHeader() {
  // Bug CSV-118: mapping is null → toMap() must return empty map per Javadoc
  CSVRecord rec = record(new String[]{"a", "b"}, null);
  Map<String, String> result = rec.toMap();
  assertNotNull("toMap should never return null", result);
  assertTrue("toMap with no headers must be empty", result.isEmpty());
  }
  @Test
  public void testToMapWithHeaders() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  mapping.put("col1", 1);
  CSVRecord rec = record(new String[]{"val0", "val1"}, mapping);
  Map<String, String> map = rec.toMap();
  assertEquals(2, map.size());
  assertEquals("val0", map.get("col0"));
  assertEquals("val1", map.get("col1"));
  }
  @Test
  public void testToMapWithHeaderMissingValues() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  mapping.put("col1", 1);
  mapping.put("col2", 2); // index 2 out of bounds for 2-element array
  CSVRecord rec = record(new String[]{"a", "b"}, mapping);
  Map<String, String> map = rec.toMap();
  assertEquals("only defined indices should be present", 2, map.size());
  assertEquals("a", map.get("col0"));
  assertEquals("b", map.get("col1"));
  assertNull("missing index should not appear", map.get("col2"));
  }
  @Test
  public void testToMapWithEmptyRecordAndHeaders() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  CSVRecord rec = record(new String[0], mapping);
  assertTrue("empty record with headers should yield empty map", rec.toMap().isEmpty());
  }
  @Test
  public void testToMapWithEmptyRecordNoHeaders() {
  CSVRecord rec = record(new String[0], null);
  assertTrue("empty record without headers should yield empty map", rec.toMap().isEmpty());
  }
  // ======================= get(String) tests =======================
  @Test
  public void testGetStringNoHeader() {
  CSVRecord rec = record(new String[]{"x"}, null);
  try {
      rec.get("any");
      fail("Expected IllegalStateException when no header mapping");
  } catch (IllegalStateException expected) {
      // expected
  }
  }
  @Test
  public void testGetStringInvalidName() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  CSVRecord rec = record(new String[]{"x"}, mapping);
  try {
      rec.get("nonexistent");
      fail("Expected IllegalArgumentException for unknown header name");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  @Test
  public void testGetStringValidName() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  CSVRecord rec = record(new String[]{"value"}, mapping);
  assertEquals("value", rec.get("col0"));
  }
  // ======================= mapping-based accessors =======================
  @Test
  public void testIsConsistent() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  assertTrue("null mapping → consistent", record(new String[]{"a"}, null).isConsistent());
  assertTrue("matching size", record(new String[]{"a"}, mapping).isConsistent());
  mapping.put("col1", 1);
  assertFalse("header larger than record", record(new String[]{"a"}, mapping).isConsistent());
  }
  @Test
  public void testIsMapped() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  CSVRecord rec = record(new String[]{"a"}, mapping);
  assertTrue(rec.isMapped("col0"));
  assertFalse(rec.isMapped("colX"));
  CSVRecord noHeader = record(new String[]{"a"}, null);
  assertFalse("isMapped on no-header must be false", noHeader.isMapped("col0"));
  }
  @Test
  public void testIsSet() {
  Map<String, Integer> mapping = new HashMap<String, Integer>();
  mapping.put("col0", 0);
  mapping.put("col1", 5); // beyond array length
  CSVRecord rec = record(new String[]{"a"}, mapping);
  assertTrue(rec.isSet("col0"));
  assertFalse("index out of range → not set", rec.isSet("col1"));
  assertFalse(record(new String[]{"a"}, null).isSet("col0"));
  }
  @Test
  public void testBasicAccessors() {
  CSVRecord rec = record(new String[]{"one", "two"}, null, "comment", 42L);
  assertEquals("comment", rec.getComment());
  assertEquals(42L, rec.getRecordNumber());
  assertEquals(2, rec.size());
  assertEquals("one", rec.get(0));
  assertEquals("two", rec.get(1));
  }
  @Test
  public void testIterator() {
  CSVRecord rec = record(new String[]{"x", "y", "z"}, null);
  Iterator<String> it = rec.iterator();
  assertTrue(it.hasNext());
  assertEquals("x", it.next());
  assertEquals("y", it.next());
  assertEquals("z", it.next());
  assertFalse(it.hasNext());
  }

}
