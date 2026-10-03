package org.jsoup.parser;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.XmlDeclaration;

 import java.util.List;

 /**
  * Tests for {@link XmlTreeBuilder} that target the bug described in issue #1015:
  * malformed XML declarations cause an IndexOutOfBoundsException.
  */
 public class XmlTreeBuilderTest {

     @Test
     public void handlesDodgyXmlDecl() {
         // malformed XML declaration "<?!>" triggers the internal hack that
         // parses the payload as an element – this must not throw.
         Document doc = Jsoup.parse("<?!>", "", Parser.xmlParser());
         assertNotNull(doc);
         // optional sanity: document should exist and have no child on the failing path
         assertTrue("expected empty or comment-only document",
                 doc.childNodes().isEmpty() ||
                 doc.childNodes().stream().allMatch(n -> n instanceof XmlDeclaration));
     }

     @Test
     public void incompleteXmlDecl() {
         Document doc = Jsoup.parse("<?xml", "", Parser.xmlParser());
         assertNotNull(doc);
         // no exception – at least a text or comment node should be present
         assertFalse(doc.childNodes().isEmpty());
     }

     @Test
     public void emptyXmlDecl() {
         Document doc = Jsoup.parse("<?>", "", Parser.xmlParser());
         assertNotNull(doc);
         assertFalse(doc.childNodes().isEmpty());
     }

     @Test
     public void normalXmlDecl() {
         Document doc = Jsoup.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "",
                 Parser.xmlParser());
         assertNotNull(doc);
         assertEquals(1, doc.childNodes().size());
         assertTrue(doc.child(0) instanceof XmlDeclaration);
         XmlDeclaration decl = (XmlDeclaration) doc.child(0);
         assertEquals("xml", decl.name());
         assertTrue(decl.attributes().hasKey("version"));
         assertEquals("1.0", decl.attributes().get("version"));
     }

     @Test
     public void missingClosingXmlDecl() {
         Document doc = Jsoup.parse("<?xml version=\"1.0\"", "",
                 Parser.xmlParser());
         assertNotNull(doc);
         // Must not throw, and the resulting doc should be non-null
         assertFalse(doc.childNodes().isEmpty());
     }

     @Test
     public void popStackToCloseGracefully() {
         // end tag for an element that doesn't exist on the stack – should be skipped
         Document doc = Jsoup.parse("<root><child/></root></nonexistent>", "",
                 Parser.xmlParser());
         assertNotNull(doc);
         // document should contain <root> with <child> inside
         Element root = doc.child(0);
         assertEquals("root", root.tagName());
         assertEquals(1, root.children().size());
         assertEquals("child", root.child(0).tagName());
     }

     @Test
     public void popStackToCloseExactMatch() {
         Document doc = Jsoup.parse("<a><b><c/></b></a>", "",
                 Parser.xmlParser());
         assertNotNull(doc);
         Element a = doc.child(0);
         assertEquals("a", a.tagName());
         Element b = a.child(0);
         Element c = b.child(0);
         assertEquals("b", b.tagName());
         assertEquals("c", c.tagName());
         assertTrue(c.children().isEmpty());
     }

     @Test
     public void insertDoctype() {
         Document doc = Jsoup.parse("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
                 "\"]8;id=md-dc02yy;http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtdhttp://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd]8;;]8;;\">", "", Parser.xmlParser());]8;;
         assertNotNull(doc);
         assertEquals(1, doc.childNodes().size());
         assertTrue(doc.child(0) instanceof DocumentType);
         DocumentType dt = (DocumentType) doc.child(0);
         assertEquals("html", dt.name());
         assertEquals("-//W3C//DTD XHTML 1.0 Strict//EN", dt.getPublicIdentifier());
         assertEquals("]8;id=md-dc02yy;http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtdhttp://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd]8;;]8;;", ]8;;
dt.getSystemIdentifier());
     }

     @Test
     public void cdataSection() {
         Document doc = Jsoup.parse("<![CDATA[data & <special>]]>", "", Parser.xmlParser());
         assertNotNull(doc);
         assertFalse(doc.childNodes().isEmpty());
         // the output is a CDataNode inside the document
         String html = doc.html();
         assertTrue(html.contains("data & <special>"));
     }

     @Test
     public void plainTextInXml() {
         Document doc = Jsoup.parse("just some text", "", Parser.xmlParser());
         assertNotNull(doc);
         assertEquals(1, doc.childNodes().size());
         assertTrue(doc.child(0) instanceof TextNode);
         assertEquals("just some text", ((TextNode) doc.child(0)).text());
     }

     @Test
     public void selfClosingUnknownTag() {
         Document doc = Jsoup.parse("<unknown/>", "", Parser.xmlParser());
         assertNotNull(doc);
         assertEquals(1, doc.childNodes().size());
         Element el = doc.child(0);
         assertEquals("unknown", el.tagName());
         assertTrue(el.children().isEmpty());
     }

     @Test
     public void multipleDodgyDeclarations() {
         // Several malformed declarations must not exhaust or damage the stack
         Document doc = Jsoup.parse("<?!><?!><?!>", "", Parser.xmlParser());
         assertNotNull(doc);
         // Each should become an XmlDeclaration (or comment) – at least no crash
         assertTrue(doc.childNodes().size() >= 1);
     }
 }
