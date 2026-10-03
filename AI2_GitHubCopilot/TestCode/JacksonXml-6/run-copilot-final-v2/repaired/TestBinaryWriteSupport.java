package com.fasterxml.jackson.dataformat.xml.ser;

 import static org.junit.Assert.*;

 import java.io.StringWriter;

 import org.junit.Test;

 import com.fasterxml.jackson.core.Base64Variant;
 import com.fasterxml.jackson.core.Base64Variants;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.dataformat.xml.XmlFactory;
 import com.fasterxml.jackson.dataformat.xml.XmlMapper;
 import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

 public class TestBinaryWriteSupport {

     /* POJO for mapper-based serialization tests */
     public static class BinaryPojo {
         @JacksonXmlProperty(localName = "field")
         public byte[] field;

         public BinaryPojo() { }

         public BinaryPojo(byte[] field) {
             this.field = field;
         }
     }

     /* ---- helper methods ---- */

     private String serializePojo(byte[] data) throws Exception {
         XmlMapper mapper = new XmlMapper();
         StringWriter sw = new StringWriter();
         mapper.writeValue(sw, new BinaryPojo(data));
         return sw.toString();
     }

     private String generateBinaryWithGenerator(byte[] data) throws Exception {
         XmlFactory f = new XmlFactory();
         StringWriter sw = new StringWriter();
         JsonGenerator gen = f.createGenerator(sw);
         gen.writeStartObject();
         gen.writeFieldName("binary");
         gen.writeBinary(data);
         gen.writeEndObject();
         gen.close();
         return sw.toString();
     }

     private String generateBinaryWithGenerator(Base64Variant v, byte[] data, int offset, int
length) throws Exception {
         XmlFactory f = new XmlFactory();
         StringWriter sw = new StringWriter();
         JsonGenerator gen = f.createGenerator(sw);
         gen.writeStartObject();
         gen.writeFieldName("binary");
         gen.writeBinary(v, data, offset, length);
         gen.writeEndObject();
         gen.close();
         return sw.toString();
     }

     /* ---- tests for mapper-based serialization of byte[] ---- */

     @Test
     public void testBinaryPojo0Bytes() throws Exception {
         String xml = serializePojo(new byte[0]);
         assertTrue("Missing <field>", xml.contains("<field>"));
         assertTrue("Missing </field>", xml.contains("</field>"));
     }

     @Test
     public void testBinaryPojo1Byte() throws Exception {
         String xml = serializePojo(new byte[] { 0x41 });
         assertTrue("Expected Base64 QQ==", xml.contains("QQ=="));
     }

     @Test
     public void testBinaryPojo2Bytes() throws Exception {
         String xml = serializePojo(new byte[] { 0x41, 0x42 });
         assertTrue("Expected Base64 QUI=", xml.contains("QUI="));
     }

     @Test
     public void testBinaryPojo3Bytes() throws Exception {
         String xml = serializePojo(new byte[] { 0x41, 0x42, 0x43 });
         assertTrue("Expected Base64 QUJD", xml.contains("QUJD"));
     }

     @Test
     public void testBinaryPojo4Bytes() throws Exception {
         String xml = serializePojo(new byte[] { 0x41, 0x42, 0x43, 0x44 });
         assertTrue("Expected Base64 QUJDRA==", xml.contains("QUJDRA=="));
     }

     @Test
     public void testBinaryPojoNull() throws Exception {
         // default serialization of null byte[] should produce <field>...</field> or <field/>
         String xml = serializePojo(null);
         assertTrue("Missing field element for null", xml.contains("<field"));
     }

     /* ---- tests for direct generator writeBinary calls ---- */

     @Test
     public void testDirectGeneratorBinaryBasic() throws Exception {
         byte[] data = new byte[] { 1, 2, 3 };
         String xml = generateBinaryWithGenerator(data);
         // Base64 of {1,2,3} is "AQID"
         assertTrue("Expected Base64 AQID", xml.contains("AQID"));
     }

     @Test
     public void testDirectGeneratorBinaryEmpty() throws Exception {
         String xml = generateBinaryWithGenerator(new byte[0]);
         assertTrue("Missing element", xml.contains("<binary"));
     }

     @Test
     public void testDirectGeneratorBinaryWithBase64Variant() throws Exception {
         byte[] data = new byte[] { 0x7f, (byte) 0x80, 0x3f };
         // "f4A/" in Base64
         String expected = Base64Variants.getDefaultVariant().encode(data);
         String xml = generateBinaryWithGenerator(Base64Variants.getDefaultVariant(), data, 0,
data.length);
         assertTrue("Expected Base64 " + expected, xml.contains(expected));
     }

     @Test
     public void testDirectGeneratorBinaryOffsetLength() throws Exception {
         byte[] full = new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55 };
         // take [1,3] -> bytes {0x22,0x33,0x44} -> Base64 "IjNE"
         String xml = generateBinaryWithGenerator(Base64Variants.getDefaultVariant(), full, 1, 3);
         assertTrue("Expected Base64 IjNE", xml.contains("IjNE"));
     }
 }
