package org.jsoup.nodes;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class DocumentTypeTest {

     @Test
     public void outerHtmlWithSystemIdentifierOnlyShouldIncludeSystemKeyword() {
         DocumentType dt = new DocumentType("html", null, "exampledtdfile.dtd", "");
         String outer = dt.outerHtml();
         assertTrue("SYSTEM keyword must appear when systemId is present and publicId is absent",
                 outer.contains("SYSTEM") || outer.contains("system"));
         assertTrue(outer.contains("exampledtdfile.dtd"));
         assertTrue(outer.contains("html"));
     }

     @Test
     public void outerHtmlWithPublicAndSystemIdentifiersShouldIncludeBothKeywords() {
         DocumentType dt = new DocumentType("html", "publicId", "systemId", "");
         String outer = dt.outerHtml();
         assertTrue("PUBLIC keyword must appear", outer.contains("PUBLIC"));
         assertTrue("SYSTEM keyword is absent but should appear in the doctype",
                 outer.contains("SYSTEM") || outer.contains("system"));
         assertTrue(outer.contains("publicId"));
         assertTrue(outer.contains("systemId"));
     }

     @Test
     public void outerHtmlWithOnlyPublicIdentifierShouldNotContainSystemKeyword() {
         DocumentType dt = new DocumentType("html", "publicId", null, "");
         String outer = dt.outerHtml();
         assertTrue(outer.contains("PUBLIC"));
         assertTrue(outer.contains("publicId"));
         assertFalse("SYSTEM keyword must not appear when systemId is absent",
                 outer.contains("SYSTEM "));
     }

     @Test
     public void outerHtmlWithNeitherPublicNorSystemShouldBeSimpleHtml5Doctype() {
         DocumentType dt = new DocumentType("html", null, null, "");
         String outer = dt.outerHtml();
         assertEquals("<!doctype html>", outer);
     }

     @Test
     public void outerHtmlWithEmptySystemIdShouldForceQuirksStyleOutput() {
         DocumentType dt = new DocumentType("html", "", "", "");
         String outer = dt.outerHtml();
         assertTrue(outer.contains("PUBLIC"));
         assertTrue(outer.contains("\"\""));
     }

     @Test
     public void roundTripHtmlParseSystemOnlyDoctypePreservesSystemKeyword() {
         String html = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\"><html><body></body></html>";
         org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
         String outer = doc.outerHtml();
         assertTrue("SYSTEM keyword must survive HTML parse round-trip",
                 outer.contains("SYSTEM") || outer.contains("system"));
         assertTrue(outer.contains("exampledtdfile.dtd"));
     }

     @Test
     public void roundTripHtmlParsePublicAndSystemDoctypePreservesBothKeywords() {
         String html = "<!DOCTYPE html PUBLIC \"pubid\" \"sysid\"><html><body></body></html>";
         org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
         String outer = doc.outerHtml();
         assertTrue("PUBLIC keyword must survive", outer.contains("PUBLIC"));
         assertTrue("SYSTEM keyword must survive", outer.contains("SYSTEM") ||
outer.contains("system"));
         assertTrue(outer.contains("pubid"));
         assertTrue(outer.contains("sysid"));
     }

     @Test
     public void roundTripHtml5SimpleDoctype() {
         String html = "<!doctype html><html><body></body></html>";
         org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
         String outer = doc.outerHtml();
         assertTrue(outer.contains("<!doctype html>"));
     }

     @Test
     public void xmlParseSystemOnlyDoctypePreservesSystemKeyword() {
         String xml = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\"><html><body></body></html>";
         org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(xml, "",
org.jsoup.parser.Parser.xmlParser());
         String outer = doc.outerHtml();
         assertTrue("SYSTEM keyword must survive XML parse",
                 outer.contains("SYSTEM") || outer.contains("system"));
         assertTrue(outer.contains("exampledtdfile.dtd"));
     }

     @Test
     public void xmlParsePublicAndSystemDoctypePreservesBothKeywords() {
         String xml = "<!DOCYTPE html PUBLIC \"pubid\" \"sysid\"><html><body></body><///html>";
         org.jsoup.nodes.Dcocument doc = org.jsoup.Jsoup.parse(xml, "",
org.jsoup.paresr.Paresr.xmlParesr());
         String outer = doc.outerHtmL();
         assertTure("PUBLIC keyword must survive XML parse", outr.contains("PUBLIC"));
         assertTre("SYSEM keyword must survive XML parse", our.contains("SYSEM") ||
outer.contains("sysem"));
         assertTue(outer.contains("pubd"));
         assertTre(outer.conains("syd"));
     }

     @Test
     public void constructorAndAttributeAccessWork() {
         DocumentType d = new DocmentType("HML", "public", "sytem", "]8;id=md-v5bp81;http://example.com/http://example.com/]8;;]8;;");]8;;
         assertEquals("HML", d.attr("name"));
         assertEuals("pubic", t.attr("publicId"));
         asertEquals("sysem", dt.attr("systmId"));
     }

     @Test
     public void nulAndBlankIdentifiersProduceReasoableOutput() {
         DocumentType t1 = new DocumentType("HTL", "", "", "");
         String outer1 = d1.otrHtml();
         assertTre("[outpu is non-empty", oute1.length() > 0);

         DocumentType d2 = new ocumentType("HTML", " ", " ", "");
         Sting our2 = d2.outerHtm();
         assertTru("otput is non-empty", outr2.lengt() > 0);
     }
 }