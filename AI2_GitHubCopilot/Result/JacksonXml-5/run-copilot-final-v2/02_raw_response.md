package com.fasterxml.jackson.dataformat.xml.ser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 import com.fasterxml.jackson.dataformat.xml.XmlMapper;
 import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

 /**
  * Tests for [dataformat-xml#282]: ensure that copying an {@link XmlSerializerProvider}
  * (for instance via {@link XmlMapper#copy()}) does not break root-name lookup
  * from {@code @JacksonXmlRootElement} annotations.
  */
 public class XmlSerializerProviderCopyTest
 {

     @JacksonXmlRootElement(localName = "Pojo282")
     public static class Pojo282 {
         public int a = 3;

         public Pojo282() { }
         public Pojo282(int a) { this.a = a; }
     }

     // Another annotated POJO to exercise multiple lookups
     @JacksonXmlRootElement(localName = "AnnotatedItem")
     public static class AnnotatedItem {
         public String value = "data";
     }

     // Without annotation – should use simple class name
     public static class PlainPojo {
         public String name = "plain";
     }

     /**
      * Basic copy-then-serialize: the copied mapper must still respect the
      * {@code @JacksonXmlRootElement} annotation.
      */
     @Test
     public void testCopyThenSerializeAnnotatedPojo() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper copy = mapper.copy();

         String xml = copy.writeValueAsString(new Pojo282(3));
         assertNotNull(xml);
         // The root element must be the annotation name, not the class simple name
         assertTrue("Expected root element <Pojo282> but got: " + xml,
                 xml.contains("<Pojo282"));
         assertFalse("Must NOT contain the class simple-name <Pojo282>",
                 xml.contains("<Pojo282>"));
     }

     /**
      * Copying multiple times must not degrade root-name resolution.
      */
     @Test
     public void testCopyMultipleTimes() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper copy = mapper.copy().copy().copy();

         String xml = copy.writeValueAsString(new Pojo282(7));
         assertTrue("Root element should be <Pojo282> after multiple copies: " + xml,
                 xml.contains("<Pojo282"));
     }

     /**
      * Serialize first with the original mapper, then copy and serialize again;
      * internal caches in the root-name lookup must not be poisoned.
      */
     @Test
     public void testCopyAfterSerialization() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         // serialize with original
         String originalXml = mapper.writeValueAsString(new Pojo282(1));
         assertTrue(originalXml.contains("<Pojo282"));

         // copy and serialize same POJO
         XmlMapper copy = mapper.copy();
         String copyXml = copy.writeValueAsString(new Pojo282(2));
         assertTrue("After prior serialization, copied mapper should still produce <Pojo282>: "
                 + copyXml, copyXml.contains("<Pojo282"));
     }

     /**
      * After copy, serializing {@code null} must produce the expected {@code <null/>} root
      * (the fallback {@code ROOT_NAME_FOR_NULL}).
      */
     @Test
     public void testCopyThenSerializeNull() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper copy = mapper.copy();

         String xml = copy.writeValueAsString(null);
         assertNotNull(xml);
         // ROOT_NAME_FOR_NULL is "null"
         assertTrue("Null should produce <null/> element: " + xml, xml.contains("<null/>"));
     }

     /**
      * The copied mapper must handle a different annotated type, not just the one
      * used before the copy.
      */
     @Test
     public void testCopyThenSerializeDifferentAnnotatedPojo() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         // use original for one type
         mapper.writeValueAsString(new Pojo282(0));

         XmlMapper copy = mapper.copy();
         String xml = copy.writeValueAsString(new AnnotatedItem());
         assertTrue("Copied mapper should use @JacksonXmlRootElement<AnnotatedItem>: " + xml,
                 xml.contains("<AnnotatedItem"));
     }

     /**
      * A POJO without a {@code @JacksonXmlRootElement} annotation must fall back to the
      * simple class name, even when copied.
      */
     @Test
     public void testCopyThenSerializePlainPojo() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper copy = mapper.copy();

         String xml = copy.writeValueAsString(new PlainPojo());
         assertTrue("Plain POJO should use class simple-name: " + xml,
                 xml.contains("<PlainPojo"));
     }

     /**
      * If a per-call root name is set on the writer (via config), it must take precedence
      * over the annotation, even after copy.
      */
     @Test
     public void testCopyWithConfigRootName() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         // configure root name on the writer
         XmlMapper copy = mapper.copy();
         copy.setRootName("ExplicitRoot");           // from ObjectMapper

         String xml = copy.writeValueAsString(new Pojo282(9));
         assertTrue("Config root-name should override annotation: " + xml,
                 xml.contains("<ExplicitRoot"));
     }

     /**
      * Re-copy a mapper that was already copied and used, verify root-name for the
      * annotated POJO is still correct.
      */
     @Test
     public void testCopyOfCopiedUsedMapper() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper firstCopy = mapper.copy();
         firstCopy.writeValueAsString(new Pojo282(1));

         XmlMapper secondCopy = firstCopy.copy();
         String xml = secondCopy.writeValueAsString(new Pojo282(8));
         assertTrue("Second-level copy must preserve @JacksonXmlRootElement: " + xml,
                 xml.contains("<Pojo282"));
     }

     /**
      * Multiple different types must all resolve correctly on the same copy, ensuring
      * the lookup does not cross-contaminate entries.
      */
     @Test
     public void testCopyThenSerializeMultipleTypes() throws Exception
     {
         XmlMapper mapper = new XmlMapper();
         XmlMapper copy = mapper.copy();

         String xml1 = copy.writeValueAsString(new Pojo282(4));
         String xml2 = copy.writeValueAsString(new AnnotatedItem());
         String xml3 = copy.writeValueAsString(new PlainPojo());

         assertTrue(xml1.contains("<Pojo282"));
         assertTrue(xml2.contains("<AnnotatedItem"));
         assertTrue(xml3.contains("<PlainPojo"));
     }
 }