package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Parser;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class XmlTreeBuilderTest {

     @Test
     public void handlesXmlDeclarationAsDeclaration() {
         // The exact trigger test case from the bug report.
         // Expected: <?xml encoding='UTF-8' ?> not <!--?xml encoding='UTF-8' ?-->
         String xml = "<?xml encoding='UTF-8' ?> <body> One </body> <!-- comment -->";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue("Output must start with the XML declaration",
                 out.startsWith("<?xml encoding='UTF-8' ?>"));
         assertTrue("Output must contain normal comment",
                 out.contains("<!-- comment -->"));
     }

     @Test
     public void xmlDeclarationWithVersion() {
         String xml = "<?xml version=\"1.0\"?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<?xml version=\"1.0\"?>"));
     }

     @Test
     public void xmlDeclarationInsideBody() {
         // XML declaration only allowed at document start; behaviour is
         // parser‑dependent – it should not be silently turned into a comment.
         String xml = "<body><?xml version=\"1.0\"?></body>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         // At minimum it must not be a comment.
         assertFalse("Declaration inside body must not become a comment",
                 out.contains("<!--?xml"));
     }

     @Test
     public void emptyDeclaration() {
         String xml = "<?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         // Should be present (maybe escaped but not a comment).
         assertFalse(out.contains("<!--?>"));
     }

     @Test
     public void declarationWithNewlines() {
         String xml = "<?xml version=\"1.0\"\nencoding=\"UTF-8\"\standalone=\"yes\"?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<?xml"));
         assertFalse(out.contains("<!--?xml"));
     }

     @Test
     public void multipleDeclarations() {
         String xml = "<?xml version=\"1.0\"?><?xml-stylesheet type=\"text/xsl\"
href=\"style.xsl\"?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<?xml-stylesheet"));
         assertFalse(out.contains("<!--?xml-stylesheet"));
     }

     @Test
     public void declarationBeforeDoctype() {
         String xml = "<?xml version=\"1.0\"?><!DOCTYPE root SYSTEM \"root.dtd\"><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.startsWith("<?xml version=\"1.0\"?>"));
         assertTrue(out.contains("<!DOCTYPE root SYSTEM \"root.dtd\">"));
     }

     @Test
     public void declarationAfterComment() {
         String xml = "<!-- comment --><?xml version=\"1.0\"?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<!-- comment -->"));
         assertTrue(out.contains("<?xml version=\"1.0\"?>"));
     }

     @Test
     public void unclosedDeclaration() {
         String xml = "<?xml version=\"1.0\"<root>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         // Underlying tokens should not produce a broken comment.
         assertFalse(out.contains("<!--xml"));
     }

     @Test
     public void declarationWithNestedLessThan() {
         String xml = "<?xml attr=\"<value>\"?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<?xml"));
         assertFalse(out.contains("<!--?xml"));
     }

     @Test
     public void whitespaceOnlyAttributesDeclaration() {
         String xml = "<?xml   ?><root/>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         assertTrue(out.contains("<?xml"));
         assertFalse(out.contains("<!--?xml"));
     }

     @Test
     public void eofInDeclaration() {
         String xml = "<?xml version=\"1.0\"";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         String out = doc.outerHtml();
         // End‑of‑file during declaration must not produce a comment with <!-- inside.
         assertFalse("EOF in declaration produced a comment", out.contains("<!--"));
     }
 }
