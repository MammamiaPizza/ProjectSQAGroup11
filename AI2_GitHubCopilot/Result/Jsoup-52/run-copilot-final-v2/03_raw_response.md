package org.jsoup.helper;

  import org.jsoup.Jsoup;
  import org.jsoup.nodes.Document;
  import org.jsoup.nodes.XmlDeclaration;
  import org.jsoup.parser.Parser;
  import org.junit.Test;

  import java.io.ByteArrayInputStream;
  import java.io.IOException;
  import java.io.InputStream;
  import java.nio.charset.StandardCharsets;

  import static org.junit.Assert.*;

  /**
   * Tests for Defects4J Jsoup bug 52b: XML declaration handling
   * - missing "?>" in serialized declaration
   * - attribute quoting normalization
   * - charset detection from XML declaration
   * - attribute parsing from bogus comments
   */
  public class Bug52Test {

      // ---- XmlDeclaration serialization ----

      @Test
      public void testXmlDeclarationToStringEndsWithQuestionMark() {
          // constructing a minimal xml declaration with attributes
          XmlDeclaration decl = new XmlDeclaration("xml", "", false);
          decl.attr("version", "1.0");
          decl.attr("encoding", "UTF-8");

          String outer = decl.toString();
          // must end with "?>"
          assertTrue("Serialized declaration must end with '?>'",
                  outer.endsWith("?>"));
      }

      @Test
      public void testXmlDeclarationGetWholeDeclarationEndsWithQuestion() {
          XmlDeclaration decl = new XmlDeclaration("xml", "", false);
          decl.attr("version", "1.0");
          decl.attr("encoding", "UTF-8");

          String whole = decl.getWholeDeclaration();
          assertTrue("getWholeDeclaration() must end with a double quote",
                  whole.endsWith("\""));
      }

      @Test
      public void testXmlDeclarationOutputUsesDoubleQuotes() throws IOException {
          // parse xml with single-quoted attributes and check serialized uses double quotes
          String xml = "<?xml version='1.0' encoding='UTF-8'?><root />";
          Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
          String outer = doc.outerHtml();
          assertTrue("Serialized declaration must use double quotes",
                  outer.contains("version=\"1.0\"") && outer.contains("encoding=\"UTF-8\""));
      }

      // ---- Attribute parsing from token ----

      @Test
      public void testXmlDeclarationAttributesParsed() throws IOException {
          String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root />";
          Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
          XmlDeclaration decl = findXmlDeclaration(doc);
          assertNotNull("Document must contain an XmlDeclaration", decl);
          assertTrue("XmlDeclaration must have attributes parsed",
                  decl.attributes().size() > 0);
      }

      @Test
      public void testXmlDeclarationAttributeValues() throws IOException {
          String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?><root />";
          Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
          XmlDeclaration decl = findXmlDeclaration(doc);
          assertNotNull(decl);
          assertEquals("version", "1.0", decl.attr("version"));
          assertEquals("encoding", "ISO-8859-1", decl.attr("encoding"));
          assertEquals("standalone", "yes", decl.attr("standalone"));
      }

      @Test
      public void testXmlDeclarationEmptyAttributes() throws IOException {
          // minimal declaration without any attributes
          String xml = "<?xml?><root />";
          Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
          XmlDeclaration decl = findXmlDeclaration(doc);
          assertNotNull(decl);
          // even empty declaration should have attributes size 0 (not fail)
          assertEquals("Attributes should be empty", 0, decl.attributes().size());
      }

      // ---- Charset detection ----

      @Test
      public void testCharsetDetectionFromXmlDeclarationIso8859() throws IOException {
          String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root />";
          Document doc = parseWithDataUtil(xml);
          assertEquals("charset must be detected from xml encoding",
                  "ISO-8859-1", doc.outputSettings().charset().name());
      }

      @Test
      public void testCharsetDetectionFromXmlDeclarationUtf8() throws IOException {
          String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root />";
          Document doc = parseWithDataUtil(xml);
          assertEquals("charset must be detected as UTF-8",
                  "UTF-8", doc.outputSettings().charset().name());
      }

      @Test
      public void testCharsetDetectionFallbackWithoutDeclaration() throws IOException {
          // no xml declaration – should default to UTF-8
          String xml = "<root />";
          Document doc = parseWithDataUtil(xml);
          assertEquals("Fallback charset should be UTF-8",
                  "UTF-8", doc.outputSettings().charset().name());
      }

      @Test
      public void testCharsetDetectionWithBomAndXmlDeclaration() throws IOException {
          // UTF-16BE BOM + XML encoding ISO-8859-1 → XML encoding wins
          String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root />";
          byte[] xmlBytes = xml.getBytes("UTF-16BE");
          byte[] bom = new byte[] { (byte) 0xFE, (byte) 0xFF };
          byte[] data = new byte[bom.length + xmlBytes.length];
          System.arraycopy(bom, 0, data, 0, bom.length);
          System.arraycopy(xmlBytes, 0, data, bom.length, xmlBytes.length);

          InputStream in = new ByteArrayInputStream(data);
          Document doc = DataUtil.load(in, null, "", Parser.xmlParser());
          in.close();
          assertEquals("charset must be detected from xml encoding even with BOM",
                  "ISO-8859-1", doc.outputSettings().charset().name());
      }

      // ---- Helper methods ----

      private XmlDeclaration findXmlDeclaration(Document doc) {
          for (org.jsoup.nodes.Node node : doc.childNodes()) {
              if (node instanceof XmlDeclaration) {
                  return (XmlDeclaration) node;
              }
          }
          return null;
      }

      private Document parseWithDataUtil(String xml) throws IOException {
          byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
          InputStream in = new ByteArrayInputStream(bytes);
          Document doc = DataUtil.load(in, null, "", Parser.xmlParser());
          in.close();
          return doc;
      }
  }