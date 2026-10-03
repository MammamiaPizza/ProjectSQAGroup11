package org.apache.commons.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class CSVParserTest {

 @Test(expected = IllegalStateException.class)
 public void testDuplicateHeaderEntries() throws IOException {
     CSVParser.parse("a,b,a\n1,2,3", CSVFormat.DEFAULT.withHeader("a", "b", "a"));
 }

 @Test(expected = IllegalStateException.class)
 public void testDuplicateHeadersFromFirstRecord() throws IOException {
     CSVParser.parse("a,a\n1,2", CSVFormat.DEFAULT.withHeader(new String[0]));
 }

 @Test(expected = IllegalStateException.class)
 public void testDuplicateHeadersWithSkipHeaderRecord() throws IOException {
     CSVParser.parse("a,b,c\n1,2,3",
             CSVFormat.DEFAULT.withHeader("a", "b", "a").withSkipHeaderRecord(true));
 }

 @Test
 public void testUniqueHeaderMapping() throws IOException {
     CSVParser parser = CSVParser.parse("a,b,c\n1,2,3", CSVFormat.DEFAULT.withHeader("a", "b",
"c"));
     Map<String, Integer> map = parser.getHeaderMap();
     assertEquals(3, map.size());
     assertEquals(Integer.valueOf(0), map.get("a"));
     assertEquals(Integer.valueOf(1), map.get("b"));
     assertEquals(Integer.valueOf(2), map.get("c"));
 }

 @Test
 public void testNoDuplicateHeadersShouldNotThrow() throws IOException {
     CSVParser parser = CSVParser.parse("x,y\n1,2", CSVFormat.DEFAULT.withHeader("x", "y"));
     assertNotNull(parser.getHeaderMap());
 }

 @Test
 public void testEmptyHeaderArrayReadsFromFirstRecord() throws IOException {
     CSVParser parser = CSVParser.parse("h1,h2\n1,2", CSVFormat.DEFAULT.withHeader(new String[0]));
     Map<String, Integer> map = parser.getHeaderMap();
     assertNotNull(map);
     assertEquals(2, map.size());
     assertEquals(Integer.valueOf(0), map.get("h1"));
     assertEquals(Integer.valueOf(1), map.get("h2"));
 }

 @Test
 public void testEmptyHeaderArrayNoDataReturnsEmptyMap() throws IOException {
     CSVParser parser = CSVParser.parse("", CSVFormat.DEFAULT.withHeader(new String[0]));
     Map<String, Integer> map = parser.getHeaderMap();
     assertNotNull(map);
     assertTrue(map.isEmpty());
 }

 @Test
 public void testNullHeaderReturnsNullHeaderMap() throws IOException {
     CSVParser parser = CSVParser.parse("a,b\n1,2", CSVFormat.DEFAULT);
     assertNull(parser.getHeaderMap());
 }

 @Test
 public void testCaseSensitiveHeadersNotDuplicate() throws IOException {
     CSVParser parser = CSVParser.parse("a,A\n1,2", CSVFormat.DEFAULT.withHeader(new String[0]));
     Map<String, Integer> map = parser.getHeaderMap();
     assertEquals(2, map.size());
     assertEquals(Integer.valueOf(0), map.get("a"));
     assertEquals(Integer.valueOf(1), map.get("A"));
 }

 @Test
 public void testSingleColumnHeader() throws IOException {
     CSVParser parser = CSVParser.parse("col\n1", CSVFormat.DEFAULT.withHeader("col"));
     Map<String, Integer> map = parser.getHeaderMap();
     assertEquals(1, map.size());
     assertEquals(Integer.valueOf(0), map.get("col"));
 }

 @Test
 public void testGetHeaderMapReturnsCopy() throws IOException {
     CSVParser parser = CSVParser.parse("x,y\n1,2", CSVFormat.DEFAULT.withHeader("x", "y"));
     Map<String, Integer> map1 = parser.getHeaderMap();
     Map<String, Integer> map2 = parser.getHeaderMap();
     assertNotSame(map1, map2);
     assertEquals(map1, map2);
     map1.clear();
     assertFalse(parser.getHeaderMap().isEmpty());
 }

}
