package org.apache.commons.csv;

 import static org.junit.Assert.*;

 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.NoSuchElementException;

 import org.junit.Test;

 public class CSVRecordTest {

     // Helper to create a mapping from header names to zero-based column indices.
     private Map<String, Integer> mapping(String... headers) {
         Map<String, Integer> map = new HashMap<String, Integer>();
         for (int i = 0; i < headers.length; i++) {
             map.put(headers[i], i);
         }
         return map;
     }

     // Helper to create a CSVRecord with given values, mapping, and comment.
     private CSVRecord record(String[] values, Map<String, Integer> mapping, String comment) {
         return new CSVRecord(values, mapping, comment, 1L);
     }

     // ---------- toMap() fault-related tests ----------

     @Test
     public void testToMapWithShortRecord() {
         // Record has 2 values, mapping expects 3 columns -> bug CSV-111
         Map<String, Integer> m = mapping("A", "B", "C");
         CSVRecord r = record(new String[]{"1", "2"}, m, null);
         Map<String, String> map = r.toMap();
         assertNotNull(map);
         assertEquals(3, map.size());
         assertEquals("1", map.get("A"));
         assertEquals("2", map.get("B"));
         assertNull(map.get("C"));          // missing column -> null, no exception
     }

     @Test
     public void testToMapWithEmptyRecord() {
         // no values, mapping expects columns
         Map<String, Integer> m = mapping("X", "Y");
         CSVRecord r = record(new String[0], m, null);
         Map<String, String> map = r.toMap();
         assertEquals(2, map.size());
         assertNull(map.get("X"));
         assertNull(map.get("Y"));
     }

     @Test
     public void testToMapWithEmptyMapping() {
         // mapping is empty, record has values
         Map<String, Integer> m = new HashMap<String, Integer>();
         CSVRecord r = record(new String[]{"a", "b"}, m, null);
         Map<String, String> map = r.toMap();
         assertTrue(map.isEmpty());
     }

     @Test
     public void testToMapNormal() {
         // matching record and mapping lengths
         Map<String, Integer> m = mapping("K1", "K2");
         CSVRecord r = record(new String[]{"v1", "v2"}, m, null);
         Map<String, String> map = r.toMap();
         assertEquals(2, map.size());
         assertEquals("v1", map.get("K1"));
         assertEquals("v2", map.get("K2"));
     }

     // ---------- putIn() variant ----------

     @Test
     public void testPutInWithShortRecord() {
         Map<String, Integer> m = mapping("A", "B", "C");
         CSVRecord r = record(new String[]{"x", "y"}, m, null);
         Map<String, String> target = new HashMap<String, String>();
         r.putIn(target);
         assertEquals(3, target.size());
         assertEquals("x", target.get("A"));
         assertEquals("y", target.get("B"));
         assertNull(target.get("C"));
     }

     // ---------- get(int) bounds ----------

     @Test
     public void testGetIntWithinRange() {
         CSVRecord r = record(new String[]{"first", "second", "third"}, null, null);
         assertEquals("first", r.get(0));
         assertEquals("second", r.get(1));
         assertEquals("third", r.get(2));
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testGetIntAtSizeThrows() {
         CSVRecord r = record(new String[]{"a", "b"}, null, null);
         r.get(2); // exactly equals size
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testGetIntBeyondSizeThrows() {
         CSVRecord r = record(new String[]{"x"}, null, null);
         r.get(5);
     }

     // ---------- get(String) bounds ----------

     @Test(expected = IllegalArgumentException.class)
     public void testGetStringWithIndexBeyondRecordThrows() {
         Map<String, Integer> m = mapping("col0", "col1");
         // only 1 value, "col1" maps to index 1 which is out of bounds
         CSVRecord r = record(new String[]{"val0"}, m, null);
         r.get("col1");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testGetStringWithInvalidNameThrows() {
         Map<String, Integer> m = mapping("name");
         CSVRecord r = record(new String[]{"data"}, m, null);
         r.get("nonExistent");
     }

     @Test(expected = IllegalStateException.class)
     public void testGetStringWithNullMappingThrows() {
         CSVRecord r = record(new String[]{"a"}, null, null);
         r.get("any");
     }

     // ---------- consistency and set-check ----------

     @Test
     public void testIsConsistent() {
         Map<String, Integer> m = mapping("a", "b");
         CSVRecord good = record(new String[]{"1", "2"}, m, null);
         CSVRecord shortRecord = record(new String[]{"1"}, m, null);
         CSVRecord longRecord = record(new String[]{"1", "2", "3"}, m, null);
         assertTrue(good.isConsistent());
         assertFalse(shortRecord.isConsistent());
         assertFalse(longRecord.isConsistent());
     }

     @Test
     public void testIsSet() {
         Map<String, Integer> m = mapping("first", "second");
         CSVRecord r = record(new String[]{"hello"}, m, null);
         assertTrue(r.isSet("first"));
         assertFalse(r.isSet("second"));   // index 1 >= values.length
     }

     // ---------- trivial attribute access ----------

     @Test
     public void testSizeAndIterator() {
         CSVRecord r = record(new String[]{"x", "y", "z"}, null, "comment");
         assertEquals(3, r.size());
         assertEquals("comment", r.getComment());
         assertEquals(1L, r.getRecordNumber());
         Iterator<String> it = r.iterator();
         assertTrue(it.hasNext());
         assertEquals("x", it.next());
         assertEquals("y", it.next());
         assertEquals("z", it.next());
         assertFalse(it.hasNext());
     }
 }