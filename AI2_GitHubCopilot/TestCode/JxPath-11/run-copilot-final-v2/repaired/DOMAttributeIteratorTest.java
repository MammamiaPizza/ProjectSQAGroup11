package org.apache.commons.jxpath.ri.model.dom;

 import java.io.StringReader;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.w3c.dom.Document;
 import org.xml.sax.InputSource;

 /**
  * Tests for namespace-aware attribute iteration in DOMAttributeIterator.
  *
  * Covers the bug where namespace-qualified attribute matching returned
  * the wrong attribute (e.g., rate:discount="10%" matched as 20%).
  */
 public class DOMAttributeIteratorTest extends TestCase {

     private Document doc;
     private JXPathContext context;

     private static final String XML =
         "<vendor xmlns:rate='http://example.com/rate' " +
         "        xmlns:rate2='http://example.com/rate2'>" +
         "  <product>" +
         "    <rate:amount rate:discount='10%' rate2:discount='20%' discount='30%'/>" +
         "  </product>" +
         "</vendor>";

     protected void setUp() throws Exception {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         doc = builder.parse(new InputSource(new StringReader(XML)));
         context = JXPathContext.newContext(doc);
         context.registerNamespace("rate", "http://example.com/rate");
         context.registerNamespace("rate2", "http://example.com/rate2");
     }

     /**
      * Namespace-qualified attribute QName must match only the attribute
      * whose namespace URI equals the bound URI.
      */
     public void testNamespaceQualifiedAttributeMatch() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@rate:discount");
         assertNotNull(ptr);
         assertEquals("10%", ptr.getValue());
     }

     /**
      * A different namespace prefix (rate2) resolves to a different URI;
      * the qualified QName must not accidentally match the rate namespace attr.
      */
     public void testDifferentNamespaceQualifiedAttribute() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@rate2:discount");
         assertNotNull(ptr);
         assertEquals("20%", ptr.getValue());
     }

     /**
      * Unqualified QName matches only attributes without a namespace URI.
      */
     public void testUnqualifiedAttributeMatch() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@discount");
         assertNotNull(ptr);
         assertEquals("30%", ptr.getValue());
     }

     /**
      * Wildcard '*' should return all attributes regardless of namespace.
      */
     public void testWildcardAttributeIteration() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@*");
         assertNotNull(ptr);
         // The wildcard returns the first attribute in document order.
         assertEquals("10%", ptr.getValue());
     }

     /**
      * Iterating with a qualified QName over an element that has the
      * attribute should yield exactly one match.
      */
     public void testQualifiedAttributeSingleResult() {
         JXPathContext amountContext = context.getRelativeContext(
             context.getPointer("vendor[1]/product[1]/rate:amount[1]"));
         Number count = (Number) amountContext.getValue("count(@rate:discount)");
         assertEquals(1.0, count.doubleValue(), 0.0);
     }

     /**
      * Iterating with an unqualified QName over an element that has the
      * unqualified attribute should yield exactly one match.
      */
     public void testUnqualifiedAttributeSingleResult() {
         JXPathContext amountContext = context.getRelativeContext(
             context.getPointer("vendor[1]/product[1]/rate:amount[1]"));
         Number count = (Number) amountContext.getValue("count(@discount)");
         assertEquals(1.0, count.doubleValue(), 0.0);
     }

     /**
      * Requesting a qualified attribute that does not exist on the element
      * should return null.
      */
     public void testMissingQualifiedAttributeReturnsNull() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@rate:nonexistent");
         assertNull(ptr);
     }

     /**
      * Requesting an unqualified attribute that does not exist on the element
      * should return null.
      */
     public void testMissingUnqualifiedAttributeReturnsNull() {
         Pointer ptr = context.getPointer(
             "vendor[1]/product[1]/rate:amount[1]/@nonexistent");
         assertNull(ptr);
     }

     /**
      * Edge case: comparing two namespace-qualified attributes on the same element
      * with a boolean expression should not confuse the namespace resolution.
      */
     public void testNamespaceAttributeComparison() {
         Boolean result = (Boolean) context.getValue(
             "vendor[1]/product[1]/rate:amount[1]/@rate:discount = '10%'");
         assertTrue(result);
         Boolean result2 = (Boolean) context.getValue(
             "vendor[1]/product[1]/rate:amount[1]/@rate2:discount = '20%'");
         assertTrue(result2);
     }
 }
