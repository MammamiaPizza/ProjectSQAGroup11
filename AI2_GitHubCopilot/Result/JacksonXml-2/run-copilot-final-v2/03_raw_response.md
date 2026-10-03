package com.fasterxml.jackson.dataformat.xml.deser;

 import static org.junit.Assert.*;
 import java.io.IOException;
 import java.io.StringReader;
 import javax.xml.stream.*;
 import org.codehaus.stax2.XMLStreamReader2;
 import com.ctc.wstx.stax.WstxInputFactory;

 import org.junit.Test;

 public class XmlTokenStreamTest {

     private XmlTokenStream createStream(String xml) throws Exception {
         XMLInputFactory f = new WstxInputFactory();
         XMLStreamReader2 sr = (XMLStreamReader2) f.createXMLStreamReader(new StringReader(xml));
         return new XmlTokenStream(sr, null);
     }

     @Test
     public void testSimpleTextContent() throws Exception {
         XmlTokenStream ts = createStream("<root>hello</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals("root", ts.getLocalName());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("hello", ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testTextBeforeChildElement() throws Exception {
         XmlTokenStream ts = createStream("<root>27<child/></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("27", ts.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals("child", ts.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testTextBeforeAndAfterChild() throws Exception {
         XmlTokenStream ts = createStream("<root>before<child/>after</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("before", ts.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("after", ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testWhitespaceOnlyText() throws Exception {
         XmlTokenStream ts = createStream("<root>   </root>");
         ts.next(); // start
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("   ", ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testEmptyElement() throws Exception {
         XmlTokenStream ts = createStream("<root/>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testAttributesFollowedByText() throws Exception {
         XmlTokenStream ts = createStream("<root attr='val'>data</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertTrue(ts.hasAttributes());
         // skip attributes to reach text
         ts.skipAttributes();
         // now expect text
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("data", ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testNestedElementsWithText() throws Exception {
         XmlTokenStream ts = createStream("<outer>text<inner/></outer>");
         // outer start
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("text", ts.getText());
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals("inner", ts.getLocalName());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
         // inner end
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next()); // outer end
     }

     @Test
     public void testCDataText() throws Exception {
         XmlTokenStream ts = createStream("<root><![CDATA[data in cdata]></root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals("data in cdata", ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testMultipleCharacterEventsConcatenation() throws Exception {
         // Woodstox may split long text or text with special characters into multiple
         // CHARACTERS events; this test ensures concatenation works.
         // Use a moderately long text with newline to encourage split.
         String longText = "line1\nline2\nline3";
         XmlTokenStream ts = createStream("<root>" + longText + "</root>");
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals(XmlTokenStream.XML_TEXT, ts.next());
         assertEquals(longText, ts.getText());
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testRepeatStartElement() throws Exception {
         XmlTokenStream ts = createStream("<root><child/><root>");
         ts.next(); // root start
         ts.next(); // child start
         // current state is START_ELEMENT for child; repeat it
         ts.repeatStartElement();
         assertEquals(XmlTokenStream.XML_START_ELEMENT, ts.next());
         assertEquals("child", ts.getLocalName());
         // then actual child end should come
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.next());
     }

     @Test
     public void testSkipEndElement() throws Exception {
         XmlTokenStream ts = createStream("<root></root>");
         ts.next(); // root start
         ts.skipEndElement(); // skips END_ELEMENT
         assertEquals(XmlTokenStream.XML_END_ELEMENT, ts.getCurrentToken());
     }

     @Test(expected = IOException.class)
     public void testSkipEndElementWhenNotEnd() throws Exception {
         XmlTokenStream ts = createStream("<root>data</root>");
         ts.next(); // start
         // next token is TEXT, not END_ELEMENT, so skipEndElement should throw
         ts.skipEndElement();
     }
 }