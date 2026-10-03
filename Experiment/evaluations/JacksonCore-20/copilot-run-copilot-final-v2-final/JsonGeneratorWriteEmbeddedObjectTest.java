import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class JsonGeneratorWriteEmbeddedObjectTest {

 private JsonGenerator newGenerator(StringWriter sw) throws IOException {
     return new JsonFactory().createGenerator(sw);
 }

 private String writeEmbedded(Object value) throws IOException {
     StringWriter sw = new StringWriter();
     JsonGenerator gen = newGenerator(sw);
     gen.writeEmbeddedObject(value);
     gen.close();
     return sw.toString();
 }

 @Test
 public void testWriteEmbeddedObjectByteArrayProducesBase64() throws Exception {
     assertEquals("\"AQID\"", writeEmbedded(new byte[] { 1, 2, 3 }));
 }

 @Test
 public void testWriteEmbeddedObjectNullProducesJsonNull() throws Exception {
     assertEquals("null", writeEmbedded(null));
 }

 @Test
 public void testWriteEmbeddedObjectEmptyByteArrayProducesEmptyString() throws Exception {
     assertEquals("\"\"", writeEmbedded(new byte[0]));
 }

 @Test
 public void testWriteEmbeddedObjectSingleByteProducesPaddedBase64() throws Exception {
     assertEquals("\"AA==\"", writeEmbedded(new byte[] { 0 }));
 }

 @Test
 public void testWriteEmbeddedObjectLargeByteArrayProducesBase64() throws Exception {
     byte[] data = new byte[999];
     StringBuilder expected = new StringBuilder(1334);
     expected.append('"');
     for (int i = 0; i < 1332; i++) {
         expected.append('A');
     }
     expected.append('"');
     assertEquals(expected.toString(), writeEmbedded(data));
 }

 @Test
 public void testWriteEmbeddedObjectInsideArray() throws Exception {
     StringWriter sw = new StringWriter();
     JsonGenerator gen = newGenerator(sw);
     gen.writeStartArray();
     gen.writeEmbeddedObject(new byte[] { 1, 2, 3 });
     gen.writeEndArray();
     gen.close();
     assertEquals("[\"AQID\"]", sw.toString());
 }

 @Test
 public void testWriteEmbeddedObjectInsideObjectField() throws Exception {
     StringWriter sw = new StringWriter();
     JsonGenerator gen = newGenerator(sw);
     gen.writeStartObject();
     gen.writeFieldName("payload");
     gen.writeEmbeddedObject(new byte[] { 1, 2, 3 });
     gen.writeEndObject();
     gen.close();
     assertEquals("{\"payload\":\"AQID\"}", sw.toString());
 }

 @Test
 public void testWriteEmbeddedObjectUnsupportedTypeThrows() throws Exception {
     JsonGenerator gen = newGenerator(new StringWriter());
     try {
         gen.writeEmbeddedObject("not-binary");
         fail("Expected JsonGenerationException for unsupported embedded object");
     } catch (JsonGenerationException e) {
         // expected
     } finally {
         gen.close();
     }
 }

 @Test
 public void testCanWriteBinaryNativelyIsFalseByDefault() throws Exception {
     JsonGenerator gen = newGenerator(new StringWriter());
     try {
         assertFalse(gen.canWriteBinaryNatively());
     } finally {
         gen.close();
     }
 }

 @Test
 public void testWriteBinaryWithOffsetAndLength() throws Exception {
     StringWriter sw = new StringWriter();
     JsonGenerator gen = newGenerator(sw);
     gen.writeBinary(new byte[] { 1, 2, 3 }, 1, 2);
     gen.close();
     assertEquals("\"AgM=\"", sw.toString());
 }

 @Test
 public void testWriteBinaryWholeArrayMatchesEmbeddedBase64() throws Exception {
     StringWriter binaryOutput = new StringWriter();
     JsonGenerator binaryGen = newGenerator(binaryOutput);
     binaryGen.writeBinary(new byte[] { 1, 2, 3 });
     binaryGen.close();

     StringWriter embeddedOutput = new StringWriter();
     JsonGenerator embeddedGen = newGenerator(embeddedOutput);
     embeddedGen.writeEmbeddedObject(new byte[] { 1, 2, 3 });
     embeddedGen.close();

     assertEquals(binaryOutput.toString(), embeddedOutput.toString());
 }

}
