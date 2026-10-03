package org.jsoup.nodes;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class DocumentTypeTest {

     @Test
     public void testConstructorWithEmptyName() {
         DocumentType dt = new DocumentType("", null, null, "");
         assertNotNull(dt);
         assertEquals("", dt.attr("name"));
     }

     @Test
     public void testConstructorWithBlankName() {
         DocumentType dt = new DocumentType(" ", null, null, "");
         assertNotNull(dt);
         assertEquals(" ", dt.attr("name"));
     }

     @Test
     public void testNodeNameIsAlwaysDoctype() {
         DocumentType dt = new DocumentType("", null, null, "");
         assertEquals("#doctype", dt.nodeName());
         dt = new DocumentType("html", null, null, "");
         assertEquals("#doctype", dt.nodeName());
     }

     @Test
     public void testOuterHtmlWithEmptyName() {
         DocumentType dt = new DocumentType("", null, null, "");
         assertEquals("<!DOCTYPE>", dt.outerHtml());
     }

     @Test
     public void testOuterHtmlWithBlankName() {
         DocumentType dt = new DocumentType(" ", null, null, "");
         assertEquals("<!DOCTYPE>", dt.outerHtml());
     }

     @Test
     public void testOuterHtmlWithValidName() {
         DocumentType dt = new DocumentType("html", null, null, "");
         assertEquals("<!DOCTYPE html>", dt.outerHtml());
     }

     @Test
     public void testOuterHtmlWithPublicId() {
         DocumentType dt = new DocumentType("html", "-//W3C//DTD HTML 4.01//EN", null, "");
         assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">", dt.outerHtml());
     }

     @Test
     public void testOuterHtmlWithSystemId() {
         DocumentType dt = new DocumentType("html", null, "]8;id=md-odlcb1;http://www.w3.org/TR/html4/strict.dtdhttp://www.w3.org/TR/html4/strict.dtd]8;;]8;;", ]8;;
"");
         assertEquals("<!DOCTYPE html \"]8;id=md-odlcb1;http://www.w3.org/TR/html4/strict.dtdhttp://www.w3.org/TR/html4/strict.dtd]8;;]8;;\">", dt.outerHtml());]8;;
     }

     @Test
     public void testOuterHtmlWithPublicAndSystemId() {
         DocumentType dt = new DocumentType("html", "-//W3C//DTD HTML 4.01//EN",
                 "]8;id=md-odlcb1;http://www.w3.org/TR/html4/strict.dtdhttp://www.w3.org/TR/html4/strict.dtd]8;;]8;;", "");]8;;
         assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"
\"]8;id=md-odlcb1;http://www.w3.org/TR/html4/strict.dtdhttp://www.w3.org/TR/html4/strict.dtd]8;;]8;;\">", dt.outerHtml());]8;;
     }

     @Test
     public void testOuterHtmlWithEmptyPublicIdNotPrinted() {
         DocumentType dt = new DocumentType("html", "", null, "");
         assertEquals("<!DOCTYPE html>", dt.outerHtml());
     }

     @Test
     public void testOuterHtmlWithBlankPublicIdNotPrinted() {
         DocumentType dt = new DocumentType("html", " ", null, "");
         assertEquals("<!DOCTYPE html>", dt.outerHtml());
     }

     @Test
     public void constructorValidationOkWithBlankName() {
         // This test directly targets the bug: blank/empty name must be accepted
         try {
             new DocumentType("", null, null, "");
         } catch (IllegalArgumentException e) {
             fail("Constructor should accept empty name, but threw: " + e.getMessage());
         }
         try {
             new DocumentType(" ", null, null, "");
         } catch (IllegalArgumentException e) {
             fail("Constructor should accept blank name, but threw: " + e.getMessage());
         }
     }

 }```