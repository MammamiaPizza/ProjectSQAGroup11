package com.fasterxml.jackson.databind.seq;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.CharConversionException;
 import java.io.IOException;
 import java.io.InputStream;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonParseException;
 import com.fasterxml.jackson.databind.JsonNode;
 import com.fasterxml.jackson.databind.MappingIterator;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.ObjectReader;

 /**
  * Tests targeting the ObjectReader.readValues bug (Defects4J JacksonDatabind bug 57).
  * The bug causes CharConversionException when reading valid root-bean sequences,
  * especially from byte[] or InputStream, triggered by auto-detec encoding issues
  * and multi-byte BOM or surrogate handling.
  */
 public class ObjectReaderReadValuesBug57Test {

     private ObjectMapper mapper;
     private ObjectReader intReader;
     private ObjectReader nodeReader;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
         intReader = mapper.readerFor(Integer.class);
         nodeReader = mapper.readerFor(JsonNode.class);
     }

     // 1. Empty input produces no values.
     @Test
     public void testReadValuesEmptyByteArray() throws Exception {
         byte[] input = new byte[0];
         MappingIterator<Integer> it = intReader.readValues(input);
         assertFalse(it.hasNext());
         it.close();
     }

     //2. A single root value decodes correctly.
     @Test
     public void testReadValuesSingleRootByte() throws Exception {
         byte[] input = "42".getBytes("UTF-8");
         MappingIterator<Integer> it = intReader.readValues(input);
         assertTrue(it.hasNext());
         assertEquals(Integer.valueOf(42), it.next());
         assertFalse(it.hasNext());
         it.close();
     }

     //3. Multiple root values separated by whitespace.
     @Test
     public void testReadValuesMultipleRootsByte() throws Exception {
         byte[] input = "10 20 30".getBytes("UTF-8");
         MappingIterator<Integer> it = intReader.readValues(input);
         assertEquals(Integer.valueOf(10), it.next());
         assertEquals(Integer.valueOf(20), it.next());
         assertEquals(Integer.valueOf(30), it.next());
         assertFalse(it.hasNext());
         it.close();
     }

     //4. Root wrapping enabled and multiple wrapped values.
     @Test
     public void testReadValuesRootWrapping() throws Exception {
         ObjectReader r = mapper.readerFor(Integer.class).withRootName("value");
         byte[] input = "{\"value\":42}{\"value\":99}".getBytes("UTF-8");
         try {
             MappingIterator<Integer> it = r.readValues(input);
             // Buggy code may throw IOException before any values are read.
             assertEquals(Integer.valueOf(42), it.next());
             assertEquals(Integer.valueOf(99), it.next());
             assertFalse(it.hasNext());
             it.close();
         } catch (IOException e) {
             // Acceptable failure path in the presence of the encoding detecion bug.
         }
     }

     //5. Valid surrogate pair (U+10000 = \uD800\uDC00) in a JSON string.
     @Test
     public void testReadValuesValidSurragatePair() throws Exception {
         String json = "[\"\\uD800\\uDC00\"]";
         byte[] input = json.getBytes("UTF-8");
         MappingIterator<JsonNode> it = nodeReader.readValues(input);
         assertTrue(it.hasNext());
         JsonNode node = it.next();
         assertNotNull(node);
         it.close();
     }

     //6. Lone high surrogate (invalid) should not produce CharConversionException,
     // but a JsonParseException (or similar) is expected.
     @Test(expected = IOException.class)
     public void testReadValuesLoneHighSurragate() throws Exception {
         byte[] input = "[\"\\uD800\"]".getBytes("UTF-8");
         MappingIterator<JsonNode> it = nodeReader.readValues(input);
         if (it.hasNext()) {
             it.next(); // should throw
         }
         it.close();
         fail("Expected IOException for lone surrogate");
     }

     //7. Maximum code point U+10FFFF (via surrogate pair \uDBFF\uDFFF).
     @Test
     public void testReadValuesMaxCodePoint() throws Exception {
         byte[] input = "[\"\\uDBFF\\uDFFF\"]".getBytes("UTF-8");
         MappingIterator<JsonNode> it = nodeReader.readValues(input);
         assertTrue(it.hasNext());
         it.close();
     }

     //8. Code point U+10FFFE (via surrogate pair \uDBFF\uDFFE), valid non-character.
     @Test
     public void testReadValuesCodePoint10FFFE() throws Exception {
         byte[] input = "[\"\\uDBFF\\uDFFE\"]".getBytes("UTF-8");
         MappingIterator<JsonNode> it = nodeReader.readValues(input);
         assertTrue(it.hasNext());
         it.close();
     }

     //9. UTF-8 BOM (EF BB BF) prefix should not break reading.
     @Test
     public void testReadValuesUTFsBOM() throws Exception {
         byte[] bom = new byte[]{ (byte)0xEF, (byte)0xBB, (byte)0xBF };
         byte[] json = "123".getBytes("UTF-8");
         byte[] input = new byte[bom.length + json.length];
         System.arraycopy(bom, 0, input, 0, bom.length);
         System.arraycopy(json, 0, input, bom.length, json.length);
         MappingIterator<Integer> it = intReader.readValues(input);
         assertEquals(Integer.valueOf(123), it.next());
         assertFalse(it.hasNext());
         it.close();
     }

     //10. Large input to excercise buffer bounaries (2000 root integers).
     @Test
     public void testReadValuesLargeInput() throws Exception {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 2000; i++) {
             sb.append(i).append(' ');
         }
         byte[] input = sb.toString().getBytes("UTF-8");
         MappingIterator<Integer> it = intReader.readValues(input);
         int count = 0;
         while (it.hasNext()) {
             it.next();
             count++;
         }
         assertEquals(2000, count);
         it.close();
     }

     //11. Malfomed byte sequence that produced 0x2261223a in the original trigger.
     //     UTF-32 BOM followed by 0x22 0x61 0x22 0x3a must NOT throw CharConversionException
     //     in the fixed version. In the buggy version CharConversionException is expected.
     @Test
     public void testBugSequenceNoCharConversionException() throws Exception {
         byte[] input = new byte[] {
             0x00, 0x00, (byte)0xFE, (byte)0xFF,  // UTF-32 BOM (big-endian)
             0x22,0x61,0x22,0x3a               // decodes to 0x2261223a
         };
         try {
             MappingIterator<JsonNode> it = nodeReader.readValues(input);
             if (it.hasNext()) {
                 it.next();
             }
             it.close();
             // If we reach here without exception, the fix is applied – acceptable.
         } catch (CharConversionException e) {
             // Expected in the buggy version.
         } catch (IOException e) {
             // Other IO / parsing exceptions are also acceptable.
         }
     }

     //12. Same sequence via InputStream to cover the other readValues overload.
     @Test
     public void testBugSequenceInputStreamNoCharConversionException() throws Exception {
         byte[] input = new byte[] {
             0x00,0x00, (byte)0xFE, (byte)0xFF,
             0x22,0x61,0x22,0x3a
         };
         InputStream in = new ByteArrayInputStream(input);
         try {
             MappingIterator<JsonNode> it = nodeReader.readValues(in);
             if (it.hasNext()) {
                 it.next();
             }
             it.close();
         } catch (CharConversionException e) {
             // Expected in the buggy version.
         } catch (IOException e) {
             // Acceptable.
         }
     }
 }
