package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CSVRecordTest {

 private CSVRecord newRecord(final String[] values, final Map<String, Integer> mapping) {
     return new CSVRecord(values, mapping, null, 1L);
 }

 private Map<String, Integer> mapping() {
     return new HashMap<String, Integer>();
 }

 @Test
 public void testGetStringByValidName() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     mapping.put("second", Integer.valueOf(1));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     assertEquals("x", record.get("first"));
     assertEquals("y", record.get("second"));
 }

 @Test
 public void testGetStringByMissingNameReturnsNull() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     final CSVRecord record = newRecord(new String[] { "x" }, mapping);

     assertNull(record.get("missing"));
 }

 @Test
 public void testGetStringInconsistentRecord() {
     // The mapping references an index beyond the available values.
     final Map<String, Integer> mapping = mapping();
     mapping.put("second", Integer.valueOf(2));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     try {
         record.get("second");
         fail("Expected IllegalArgumentException for an inconsistent record");
     } catch (final IllegalArgumentException expected) {
         // expected
     }
 }

 @Test
 public void testGetStringByIndexJustWithinRange() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("last", Integer.valueOf(1));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     assertEquals("y", record.get("last"));
 }

 @Test
 public void testGetStringByIndexJustOutsideRange() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("overflow", Integer.valueOf(2));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     try {
         record.get("overflow");
         fail("Expected IllegalArgumentException for a mapping index equal to the record size");
     } catch (final IllegalArgumentException expected) {
         // expected
     }
 }

 @Test
 public void testGetStringWhenNoMappingThrowsIllegalStateException() {
     final CSVRecord record = newRecord(new String[] { "x" }, null);

     try {
         record.get("first");
         fail("Expected IllegalStateException when no header mapping is provided");
     } catch (final IllegalStateException expected) {
         // expected
     }
 }

 @Test
 public void testIsConsistentMatchesSize() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     mapping.put("second", Integer.valueOf(1));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     assertTrue(record.isConsistent());
 }

 @Test
 public void testIsConsistentFalseWhenSizesDiffer() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     mapping.put("second", Integer.valueOf(1));
     mapping.put("third", Integer.valueOf(2));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     assertFalse(record.isConsistent());
 }

 @Test
 public void testIsConsistentTrueWhenNoMapping() {
     final CSVRecord record = newRecord(new String[] { "x", "y" }, null);

     assertTrue(record.isConsistent());
 }

 @Test
 public void testIsMapped() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     final CSVRecord record = newRecord(new String[] { "x" }, mapping);

     assertTrue(record.isMapped("first"));
     assertFalse(record.isMapped("other"));

     final CSVRecord unmapped = newRecord(new String[] { "x" }, null);
     assertFalse(unmapped.isMapped("first"));
 }

 @Test
 public void testIsSetWithinAndOutsideRange() {
     final Map<String, Integer> mapping = mapping();
     mapping.put("first", Integer.valueOf(0));
     mapping.put("second", Integer.valueOf(2));
     final CSVRecord record = newRecord(new String[] { "x", "y" }, mapping);

     assertTrue(record.isSet("first"));
     assertFalse(record.isSet("second"));
     assertFalse(record.isSet("unknown"));
 }

 @Test
 public void testGetByIndex() {
     final CSVRecord record = newRecord(new String[] { "a", "b" }, null);

     assertEquals("a", record.get(0));
     assertEquals("b", record.get(1));
 }

@Test
 public void testGetRecordNumber() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     final CSVRecord record = new CSVRecord(new String[]{"x"}, mapping, null, 42L);
     assertEquals(42L, record.getRecordNumber());
 }

 @Test
 public void testGetComment() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     final CSVRecord recordWithComment = new CSVRecord(new String[]{"x"}, mapping, "test comment",
1L);
     assertEquals("test comment", recordWithComment.getComment());
     final CSVRecord recordWithoutComment = new CSVRecord(new String[]{"x"}, mapping, null, 1L);
     assertNull(recordWithoutComment.getComment());
 }

 @Test
 public void testSize() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     mapping.put("second", 1);
     final CSVRecord record = new CSVRecord(new String[]{"x", "y"}, mapping, null, 1L);
     assertEquals(2, record.size());
 }

 @Test
 public void testNullValuesYieldsEmptyRecord() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     final CSVRecord record = new CSVRecord(null, mapping, null, 1L);
     assertEquals(0, record.size());
     assertTrue(record.isConsistent());
 }
}
