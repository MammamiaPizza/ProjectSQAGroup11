package com.fasterxml.jackson.dataformat.xml.lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import org.junit.Test;

/**

 - Tests for {@link com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser} focusing on
 - nested unwrapped list parsing, especially empty child elements (bug #180).
  */
 public class FromXmlParserNestedUnwrappedListsTest {
  private final XmlMapper mapper = new XmlMapper();
  // --- Simple unwrapped list of strings ---
  @JacksonXmlRootElement(localName = "root")
  public static class StringListWrapper {
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "item")
  public List<String> items;
  }
  // Single empty element should produce list of size 1
  @Test
  public void testSingleEmptyElement() throws Exception {
  String xml = "<root><item></item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertNotNull(result.items);
  assertEquals("Expected one empty string in list", 1, result.items.size());
  }
  // Two consecutive empty elements
  @Test
  public void testTwoConsecutiveEmptyElements() throws Exception {
  String xml = "<root><item></item><item></item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(2, result.items.size());
  }
  // Empty element followed by populated element (boundary)
  @Test
  public void testEmptyThenPopulated() throws Exception {
  String xml = "<root><item></item><item>value</item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(2, result.items.size());
  assertEquals("", result.items.get(0));
  assertEquals("value", result.items.get(1));
  }
  // Populated element followed by empty element
  @Test
  public void testPopulatedThenEmpty() throws Exception {
  String xml = "<root><item>first</item><item></item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(2, result.items.size());
  assertEquals("first", result.items.get(0));
  assertEquals("", result.items.get(1));
  }
  // Empty element at start and end of a parent list (boundary)
  @Test
  public void testEmptyAtStartAndEnd() throws Exception {
  String xml = "<root><item></item><item>middle</item><item></item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(3, result.items.size());
  assertEquals("", result.items.get(0));
  assertEquals("middle", result.items.get(1));
  assertEquals("", result.items.get(2));
  }
  // All empty elements only
  @Test
  public void testAllEmptyElements() throws Exception {
  String xml = "<root><item></item><item></item><item></item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(3, result.items.size());
  }
  // Normal non-empty elements (sanity check)
  @Test
  public void testNormalNonEmptyList() throws Exception {
  String xml = "<root><item>a</item><item>b</item></root>";
  StringListWrapper result = mapper.readValue(xml, StringListWrapper.class);
  assertEquals(2, result.items.size());
  assertEquals("a", result.items.get(0));
  assertEquals("b", result.items.get(1));
  }
  // --- Nested unwrapped lists (inner list as object field) ---
  public static class InnerItem {
  @JacksonXmlText
  public String value;
  }
  public static class OuterItem {
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "inner")
  public List<InnerItem> inners;
  }
  @JacksonXmlRootElement(localName = "root")
  public static class NestedUnwrappedWrapper {
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "outer")
  public List<OuterItem> outers;
  }
  // Nested unwrapped list with a single empty inner element
  @Test
  public void testNestedUnwrappedSingleEmpty() throws Exception {
  String xml = "<root><outer><inner></inner></outer></root>";
  NestedUnwrappedWrapper result = mapper.readValue(xml, NestedUnwrappedWrapper.class);
  assertNotNull(result.outers);
  assertEquals(1, result.outers.size());
  OuterItem outer = result.outers.get(0);
  assertNotNull(outer.inners);
  assertEquals(1, outer.inners.size());
  assertNull(outer.inners.get(0).value); // empty element gives null text
  }
  // Nested unwrapped list with two consecutive empty inner elements
  @Test
  public void testNestedTwoConsecutiveEmpty() throws Exception {
  String xml = "<root><outer><inner></inner><inner></inner></outer></root>";
  NestedUnwrappedWrapper result = mapper.readValue(xml, NestedUnwrappedWrapper.class);
  assertEquals(1, result.outers.size());
  OuterItem outer = result.outers.get(0);
  assertEquals(2, outer.inners.size());
  }
  // Mixed empty and non-empty inner elements
  @Test
  public void testNestedMixedEmptyAndPopulated() throws Exception {
  String xml = "<root><outer><inner></inner><inner>text</inner><inner></inner></outer></root>";
  NestedUnwrappedWrapper result = mapper.readValue(xml, NestedUnwrappedWrapper.class);
  assertEquals(1, result.outers.size());
  OuterItem outer = result.outers.get(0);
  assertEquals(3, outer.inners.size());
  assertNull(outer.inners.get(0).value);
  assertEquals("text", outer.inners.get(1).value);
  assertNull(outer.inners.get(2).value);
  }

}