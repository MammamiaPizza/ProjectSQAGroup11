package com.fasterxml.jackson.dataformat.xml.deser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;

 import java.io.IOException;
 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.dataformat.xml.XmlMapper;
 import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
 import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

 public class FromXmlParserBug1Test {

     private final XmlMapper mapper = new XmlMapper();

     // Simple bean with an unwrapped list of strings
     public static class SimpleUnwrappedList {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "item")
         public List<String> items;
     }

     // Bean with nested unwrapped lists
     public static class RootWithNested {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "items")
         public List<ItemWrapper> items;
     }

     public static class ItemWrapper {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "sub")
         public List<String> subItems;
     }

     // Deep nesting support
     public static class RootDeep {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "level1")
         public List<Level1> level1List;
     }

     public static class Level1 {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "level2")
         public List<Level2> level2List;
     }

     public static class Level2 {
         @JacksonXmlElementWrapper(useWrapping = false)
         @JacksonXmlProperty(localName = "leaf")
         public List<String> leafList;
     }

     @Test
     public void testEmptyElementInUnwrappedList() throws IOException {
         String xml = "<SimpleUnwrappedList><item></item></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected one empty string entry", 1, result.items.size());
         assertEquals("", result.items.get(0));
     }

     @Test
     public void testSelfClosingEmptyElement() throws IOException {
         String xml = "<SimpleUnwrappedList><item/></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected one empty string entry for self-closing tag", 1,
result.items.size());
         assertEquals("", result.items.get(0));
     }

     @Test
     public void testNestedUnwrappedEmptySubElement() throws IOException {
         String xml = "<RootWithNested><items><sub></sub></items></RootWithNested>";
         RootWithNested result = mapper.readValue(xml, RootWithNested.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected one outer item", 1, result.items.size());
         ItemWrapper wrapper = result.items.get(0);
         assertNotNull(wrapper);
         assertNotNull(wrapper.subItems);
         assertEquals("Expected one empty string in nested list", 1, wrapper.subItems.size());
         assertEquals("", wrapper.subItems.get(0));
     }

     @Test
     public void testNestedUnwrappedSelfClosingSubElement() throws IOException {
         String xml = "<RootWithNested><items><sub/></items></RootWithNested>";
         RootWithNested result = mapper.readValue(xml, RootWithNested.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals(1, result.items.size());
         ItemWrapper wrapper = result.items.get(0);
         assertNotNull(wrapper);
         assertNotNull(wrapper.subItems);
         assertEquals(1, wrapper.subItems.size());
         assertEquals("", wrapper.subItems.get(0));
     }

     @Test
     public void testMixedEmptyAndNonEmptyInUnwrapped() throws IOException {
         String xml =
"<SimpleUnwrappedList><item></item><item>value</item><item/></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected three entries", 3, result.items.size());
         assertEquals("", result.items.get(0));
         assertEquals("value", result.items.get(1));
         assertEquals("", result.items.get(2));
     }

     @Test
     public void testMultipleEmptySiblings() throws IOException {
         String xml = "<SimpleUnwrappedList><item></item><item/></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected two empty entries", 2, result.items.size());
         assertEquals("", result.items.get(0));
         assertEquals("", result.items.get(1));
     }

     @Test
     public void testNonEmptyElementRemains() throws IOException {
         String xml = "<SimpleUnwrappedList><item>some text</item></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected one non-empty entry", 1, result.items.size());
         assertEquals("some text", result.items.get(0));
     }

     @Test
     public void testEmptyListWithNothing() throws IOException {
         String xml = "<SimpleUnwrappedList></SimpleUnwrappedList>";
         SimpleUnwrappedList result = mapper.readValue(xml, SimpleUnwrappedList.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals("Expected empty list", 0, result.items.size());
     }

     @Test
     public void testNestedMultipleEmptySubElements() throws IOException {
         String xml = "<RootWithNested><items><sub></sub><sub/></items></RootWithNested>";
         RootWithNested result = mapper.readValue(xml, RootWithNested.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals(1, result.items.size());
         ItemWrapper wrapper = result.items.get(0);
         assertNotNull(wrapper.subItems);
         assertEquals("Expected two empty strings in nested list", 2, wrapper.subItems.size());
         assertEquals("", wrapper.subItems.get(0));
         assertEquals("", wrapper.subItems.get(1));
     }

     @Test
     public void testNestedMixedEmptyAndNonEmpty() throws IOException {
         String xml = "<RootWithNested><items><sub></sub><sub>hello</sub></items></RootWithNested>";
         RootWithNested result = mapper.readValue(xml, RootWithNested.class);
         assertNotNull(result);
         assertNotNull(result.items);
         assertEquals(1, result.items.size());
         ItemWrapper wrapper = result.items.get(0);
         assertNotNull(wrapper.subItems);
         assertEquals(2, wrapper.subItems.size());
         assertEquals("", wrapper.subItems.get(0));
         assertEquals("hello", wrapper.subItems.get(1));
     }

     @Test
     public void testDeepNestedEmptyLeaf() throws IOException {
         String xml = "<RootDeep><level1><level2><leaf></leaf></level2></level1></RootDeep>";
         RootDeep result = mapper.readValue(xml, RootDeep.class);
         assertNotNull(result);
         assertNotNull(result.level1List);
         assertEquals(1, result.level1List.size());
         Level1 l1 = result.level1List.get(0);
         assertNotNull(l1.level2List);
         assertEquals(1, l1.level2List.size());
         Level2 l2 = l1.level2List.get(0);
         assertNotNull(l2.leafList);
         assertEquals("Expected one empty string in deep nested list", 1, l2.leafList.size());
         assertEquals("", l2.leafList.get(0));
     }

     @Test
     public void testDeepNestedSelfClosingEmptyLeaf() throws IOException {
         String xml = "<RootDeep><level1><level2><leaf/></level2></level1></RootDeep>";
         RootDeep result = mapper.readValue(xml, RootDeep.class);
         assertNotNull(result);
         assertNotNull(result.level1List);
         assertEquals(1, result.level1List.size());
         Level1 l1 = result.level1List.get(0);
         assertNotNull(l1.level2List);
         assertEquals(1, l1.level2List.size());
         Level2 l2 = l1.level2List.get(0);
         assertNotNull(l2.leafList);
         assertEquals(1, l2.leafList.size());
         assertEquals("", l2.leafList.get(0));
     }
 }
