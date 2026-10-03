package com.fasterxml.jackson.core.json;

 import java.io.*;
 import java.math.BigDecimal;

 import com.fasterxml.jackson.core.*;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for BigDecimal writing behavior, focusing on the WRITE_BIGDECIMAL_AS_PLAIN feature
  * and the fix for bug #315 where extremely large BigDecimal values with plain mode
  * should throw an exception instead of attempting to write an enormous string.
  */
 public class TestBigDecimalPlainWriting
 {
     // ---- Normal values without plain mode ----

     @Test
     public void testNormalBigDecimal() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.writeStartArray();
         g.writeNumber(new BigDecimal("123.456"));
         g.writeNumber(new BigDecimal("0.001"));
         g.writeNumber(new BigDecimal("1E+2"));
         g.writeEndArray();
         g.close();
         assertEquals("[123.456,0.001,1E+2]", sw.toString());
     }

     // ---- Normal values with plain mode ----

     @Test
     public void testNormalBigDecimalPlain() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         g.writeNumber(new BigDecimal("1E+2"));
         g.writeNumber(new BigDecimal("1E-5"));
         g.writeEndArray();
         g.close();
         assertEquals("[100,0.00001]", sw.toString());
     }

     // ---- Trigger case: huge value with plain mode MUST throw ----

     @Test
     public void testTooBigBigDecimal() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         try {
             g.writeNumber(new BigDecimal("1E+10000"));
             g.close();
             fail("Should not have written without exception: 1E+10000");
         } catch (IOException e) {
             // Expected: value too large for plain representation
         }
     }

     @Test
     public void testTooBigBigDecimalUTF8() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(bos);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         try {
             g.writeNumber(new BigDecimal("1E+10000"));
             g.close();
             fail("Should not have written without exception: 1E+10000 (UTF-8)");
         } catch (IOException e) {
             // Expected: value too large for plain representation
         }
     }

     @Test
     public void testTooBigNegativeBigDecimal() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         try {
             g.writeNumber(new BigDecimal("-1E+10000"));
             g.close();
             fail("Should have thrown for -1E+10000");
         } catch (IOException e) {
             // Expected
         }
     }

     // ---- Scientific notation: large exponent WITHOUT plain mode is fine ----

     @Test
     public void testLargeExponentScientificNotation() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         // WRITE_BIGDECIMAL_AS_PLAIN is NOT enabled
         g.writeStartArray();
         g.writeNumber(new BigDecimal("1E+100"));
         g.writeNumber(new BigDecimal("1E+10000"));
         g.writeEndArray();
         g.close();
         assertEquals("[1E+100,1E+10000]", sw.toString());
     }

     // ---- Edge cases ----

     @Test
     public void testZeroBigDecimalPlain() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         g.writeNumber(BigDecimal.ZERO);
         g.writeEndArray();
         g.close();
         assertEquals("[0]", sw.toString());
     }

     @Test
     public void testVerySmallDecimalPlain() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartArray();
         g.writeNumber(new BigDecimal("1E-10"));
         g.writeEndArray();
         g.close();
         assertEquals("[0.0000000001]", sw.toString());
     }

     @Test
     public void testMultiplePlainValuesInObject() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.writeStartObject();
         g.writeNumberField("small", new BigDecimal("0.5"));
         g.writeNumberField("medium", new BigDecimal("12345.6789"));
         g.writeEndObject();
         g.close();
         assertEquals("{\"small\":0.5,\"medium\":12345.6789}", sw.toString());
     }

     // ---- Interaction with WRITE_NUMBERS_AS_STRINGS ----

     @Test
     public void testNumbersAsStringsAndPlain() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
         g.writeStartArray();
         g.writeNumber(new BigDecimal("1E+3"));
         g.writeEndArray();
         g.close();
         assertEquals("[\"1000\"]", sw.toString());
     }

     @Test
     public void testTooBigNumbersAsStringsAndPlain() throws Exception {
         StringWriter sw = new StringWriter();
         JsonFactory f = new JsonFactory();
         JsonGenerator g = f.createGenerator(sw);
         g.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
         g.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
         g.writeStartArray();
         try {
             g.writeNumber(new BigDecimal("1E+10000"));
             g.close();
             fail("Should have thrown for huge value with numbers-as-strings and plain mode");
         } catch (IOException e) {
             // Expected: plain representation still too large even as a string
         }
     }
 }