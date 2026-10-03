package com.fasterxml.jackson.dataformat.xml.deser;

 import java.io.*;
 import javax.xml.stream.*;

 import org.junit.*;
 import static org.junit.Assert.*;

 public class XmlTokenStreamTest {

     private XMLStreamReader createReader(String xml) throws XMLStreamException {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
         return factory.createXMLStreamReader(new StringReader(xml));
     }

     private XmlTokenStream createTokenStream(String xml) throws XMLStreamException {
         return new XmlTokenStream(createReader(xml), null);
     }

     @Test
     public void testSimpleText() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>hello</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("hello", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testMixedContentTextBeforeChild() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>abc<child/></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("abc", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testMixedContentTextAfterChild() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root><child/>def</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("def", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testMixedContentTextBeforeAndAfter() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>abc<child/>def</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("abc", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("def", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testEmptyElement() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testWhitespaceOnly() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>   </root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         // whitespace-only text should be ignored (not produce XML_TEXT)
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testMultipleMixedSegments() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>a<b/>c<d/>e</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("a", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("b", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("b", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("c", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("d", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("d", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("e", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testCDATAText() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root><![CDATA[hello]]></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("hello", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testRepeatStartElement() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root><list><item>value</item></list></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("list", tokens.getLocalName());
         tokens.repeatStartElement();
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("list", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("item", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("value", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("item", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("list", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testRepeatStartElementWithText() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root><elem>text1<child/>text2</elem></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("elem", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("text1", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("child", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("text2", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("elem", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }

     @Test
     public void testNestedMixedContent() throws Exception {
         XmlTokenStream tokens = createTokenStream("<root>a<b>c<d/>e</b>f</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("a", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("b", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("c", tokens.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, tokens.next());
         assertEquals("d", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("d", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("e", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("b", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, tokens.next());
         assertEquals("f", tokens.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, tokens.next());
         assertEquals("root", tokens.getLocalName());
         assertEquals(XmlTokenStream.XML_END, tokens.next());
     }
 }
