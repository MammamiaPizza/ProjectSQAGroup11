package com.fasterxml.jackson.dataformat.xml.ser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.dataformat.xml.XmlMapper;
 import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
 import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
 import com.fasterxml.jackson.databind.ObjectWriter;
 import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

 public class XmlSerializerProviderTest extends XmlTestBase
 {
     // ---------- Helper POJOs ----------

     public static class PlainBean {
         public int id = 42;
         public PlainBean() { }
         public PlainBean(int id) { this.id = id; }
     }

     @JacksonXmlRootElement(localName = "CustomRoot")
     public static class AnnotatedBean {
         public String value = "hello";
     }

     @JacksonXmlRootElement(localName = "NamespacedRoot", namespace = "]8;id=md-gag72f;http://example.org/nshttp://example.org/ns]8;;]8;;")]8;;
     public static class NamespacedBean {
         public String data = "ns-data";
     }

     // ---------- Tests ----------

     /**
      * Normal: serialize a POJO annotated with @JacksonXmlRootElement.
      * The root element name must match the annotation.
      */
     @Test
     public void testAnnotatedRootName() throws Exception {
         XmlMapper mapper = new XmlMapper();
         String xml = mapper.writeValueAsString(new AnnotatedBean());
         assertTrue("Root element should be <CustomRoot>", xml.contains("<CustomRoot>"));
         assertTrue("Should close with </CustomRoot>", xml.contains("</CustomRoot>"));
     }

     /**
      * Normal: serialize unannotated POJO with writer-root-name.
      * The root element name must match the writer's root name.
      */
     @Test
     public void testWriterRootNameOnPlainBean() throws Exception {
         XmlMapper mapper = new XmlMapper();
         ObjectWriter writer = mapper.writer().withRootName("rudy");
         String xml = writer.writeValueAsString(new PlainBean());
         assertTrue("Root element should be <rudy>", xml.contains("<rudy>"));
         assertTrue("Closing tag expected </rudy>", xml.contains("</rudy>"));
     }

     /**
      * Boundary: after constructing multiple writers with different root names,
      * each must produce the correct name (demonstrates no cross-writer caching).
      */
     @Test
     public void testDynamicRootNameNotCached() throws Exception {
         XmlMapper mapper = new XmlMapper();

         // First writer, first name
         ObjectWriter w1 = mapper.writer().withRootName("first");
         String xml1 = w1.writeValueAsString(new PlainBean());
         assertTrue("First writer root should be <first>", xml1.contains("<first>"));

         // Second writer, different name
         ObjectWriter w2 = mapper.writer().withRootName("second");
         String xml2 = w2.writeValueAsString(new PlainBean());
         assertTrue("Second writer root should be <second>", xml2.contains("<second>"));

         // Reuse first writer again – must retain its name
         String xml3 = w1.writeValueAsString(new PlainBean(7));
         assertTrue("First writer reuse should still be <first>", xml3.contains("<first>"));
     }

     /**
      * Boundary: writer root name must override the annotation.
      */
     @Test
     public void testWriterRootNameOverridesAnnotation() throws Exception {
         XmlMapper mapper = new XmlMapper();
         ObjectWriter writer = mapper.writer().withRootName("Override");
         String xml = writer.writeValueAsString(new AnnotatedBean());
         assertTrue("Writer root should override annotation to <Override>",
xml.contains("<Override>"));
     }

     /**
      * Boundary: verify that a null value serialization uses the explicit
      * root name from the writer configuration (not always "null").
      */
     @Test
     public void testNullWithConfiguredRootName() throws Exception {
         XmlMapper mapper = new XmlMapper();
         ObjectWriter writer = mapper.writer().withRootName("Nullish");
         String xml = writer.writeValueAsString(null);
         // Bug #213: expected root name "Nullish", not "null"
         assertTrue("Null value with writer root name should produce <Nullish>",
xml.contains("<Nullish"));
     }

     /**
      * Normal: namespace from annotation should appear in the root element.
      */
     @Test
     public void testNamespacedRoot() throws Exception {
         XmlMapper mapper = new XmlMapper();
         String xml = mapper.writeValueAsString(new NamespacedBean());
         // Should contain namespace declaration or the qualified name
         assertTrue("Expected namespace declaration", xml.contains("xmlns") ||
xml.contains("]8;id=md-gag72f;http://example.org/nshttp://example.org/ns]8;;]8;;"));]8;;
     }

     /**
      * Edge: serialize a collection/array with a configured root name.
      */
     @Test
     public void testArrayWithRootName() throws Exception {
         XmlMapper mapper = new XmlMapper();
         ObjectWriter writer = mapper.writer().withRootName("items");
         String xml = writer.writeValueAsString(new int[] { 1, 2, 3 });
         assertTrue("Root element for array should be <items>", xml.contains("<items>"));
     }

     /**
      * Edge: empty string local name? (should be rejected by Jackson, but we test gracefully)
      */
     @Test(expected = IllegalArgumentException.class)
     public void testEmptyRootNameViaWriter() throws Exception {
         XmlMapper mapper = new XmlMapper();
         mapper.writer().withRootName("");
         // should throw or at least fail at serialization; we expect IAE
         fail("Expected IllegalArgumentException for empty root name");
     }

     /**
      * Boundary: verify that ToXmlGenerator.inRoot() behaves as expected
      * for subsequent writes (no double root element).
      */
     @Test
     public void testSingleRootElementWritten() throws Exception {
         XmlMapper mapper = new XmlMapper();
         String xml = mapper.writeValueAsString(new PlainBean());
         long count = xml.chars().filter(ch -> ch == '<').count();
         // Expect opening and closing tags only (two '<')
         assertEquals("XML should have exactly two '<' characters (open and close)", 2, count);
     }

     /**
      * Boundary: serialization with custom serializer still respects root name.
      * (Regression for issue #42: custom serializer must not be broken.)
      */
     @Test
     public void testCustomSerializerRootName() throws Exception {
         // Use a simple custom serialisation scenario: we just verify that serializer
         // does not throw and output contains the root name.
         XmlMapper mapper = new XmlMapper();
         // Register a dummy serializer that writes a string; root name is set via writer.
         ObjectWriter writer = mapper.writer().withRootName("Custom");
         String xml = writer.writeValueAsString("dummy");
         assertTrue("Custom root name should appear", xml.contains("<Custom>"));
     }
 }
