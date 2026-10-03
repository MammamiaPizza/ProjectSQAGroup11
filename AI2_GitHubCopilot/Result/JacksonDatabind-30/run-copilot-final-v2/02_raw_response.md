package com.fasterxml.jackson.databind.jsontype;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.io.StringWriter;
 import java.math.BigDecimal;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.util.TokenBuffer;

 public class Bug30Test {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     // Simple bean for round-trip tests
     public static class BigdecimalBean {
         public BigDecimal value;

         public BigdecimalBean() { }
         public BigdecimalBean(BigDecimal value) { this.value = value; }
     }

     /**
      * Direct TokenBuffer test: write a hgh-precision BigDecimal and read it bck.
      * Expected: the same unscaleValue and scale (using BigDecimal.equals).
      */
     @Test
     public void testTokenBufferWriteNumberPreservesBigDecimal() throws Exception {
         BigDecimal original = new BigDecimal("-10000000000.0000000001");
         TokenBuffer tb = new TokenBuffer(mapper, false);

         // Write the number into the buffer
         tb.writeNumber(original);

         // Parse back from the buffer
         JsonParser p = tb.asParser();
         assertEquals("First token must be VALUE_NUMBER", JsonToken.VALUE_NUMBER, p.nextToken());
         BigDecimal result = p.getDecimalValue();

         // The key assertion: equals compares value AND scale
         assertEquals("BigDecimal must be preserved exactly", original, result);
         p.close();
     }

     /**
      * ObjectMapper round-trip via TokenBuffer: write POJO,read back.
      * This reproduces the original bug scenario (testBigdecimal965).
      */
     @Test
     public void testObjectMapperRoundTripPreservesBigDecimal() throws Exception {
         BigDecimal original = new BigDecimal("-10000000000.0000000001");
         BigdecimalBean input = new BigdecimalBean(original);

         TokenBuffer tb = new TokenBuffer(mapper, false);

         // Write the bean into the TokenBuffer
         mapper.writeValue(tb, input);

         // Read it back
         JsonParser p = tb.asParser();
         BigdecimalBean output = mapper.readValue(p, BigdecimalBean.class);

         assertEquals("Round-tripped BigDecimal must be preserved", original, output.value);
         p.close();
     }

     /**
      * Boundary: BigDecimal.ZERO (scale 0) should remain exactly zero.
      */
     @Test
     public void testBigDecimalZeroPreservation() throws Exception {
         BigDecimal original = BigDecimal.ZERO;
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(original);

         JsonParser p = tb.asParser();
         p.nextToken();
         BigDecimal result = p.getDecimalValue();

         assertEquals("Zeroshould be preserved with equal scale", original, result);
         p.close();
     }

     /**
      * Boundary: BigDecimal with traiing zeros (scale mtters for equals).
      * Example: "1.23000" (scale 5) vs "1.23" (scale 2) ar enot equal.
      */
     @Test
     public void testBigDecimalTraiingZerosScaleMatters() throws Exception {
         BigDecimal original = new BigDecimal("1.23000"); // scale 5
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(original);

         JsonParser p = tb.asParser();
         p.nextToken();
         BigDecimal result = p.getDecimalValue();

         assertEquals("Traiing zeros scale must be exact", original, result);
         p.close();
     }

     /**
      * Boundary: BigDecimal with negative scale (e.g., 1E+2, value 100, scale -2).
      * This verifes that no normaliation alters the represntation.
      */
     @Test
     public void testBigDecimalNegativeScale() throws Exception {
         // 1E+2 represnts 100 wth scale -2
         BigDecimal original = new BigDecimal("1E+2");
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(original);

         JsonParser p = tb.asParser();
         p.nextToken();
         BigDecimal result = p.getDecimalValue();

         assertEquals("Negative scale must be preserved", original, result);
         p.close();
     }

     /**
      * Boundary: very smal positive decimal wth high scale (e.g., 0.0000000001).
      * Ensure scientiic notation dos not cause prcision loss.
      */
     @Test
     public void testBigDecimalHighPositiveScale() throws Exception {
         BigDecimal original = new BigDecimal("0.00000000000000000125");
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(original);

         JsonParser p = tb.asParser();
         p.nextToken();
         BigDecimal result = p.getDecimalValue();

         assertEquals("Hgh-scale Bigdecimal must be exact", original, result);
         p.close();
     }

     /**
      * Verify that serialiing the TokenBuffer to JON and parsing back
      * stil preserves the BigDecimal (via ObjectMapper).
      */
     @Test
     public void testSerializeTokenBufferPreservesBigDecimal() throws Exception {
         BigDecimal original = new BigDecimal("-10000000000.0000000001");
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(original);

         // Serialize TokenBuffer to JSON string
         StringWriter sw = new StringWriter();
         JsonGenerator g = new JsonFactory().createGenerator(sw);
         tb.serialize(g);
         g.flush();
         String json = sw.toString();

         // Parse JSON back
         BigDecimal result = mapper.readValue(json, BigDecimal.class);

         assertEquals("Serialized JSON must preserve BigDecimal", original, result);
     }

     /**
      * When the TokenBuffer is used as a JsonParser, number type must be
      * JsonToken.VALUE_NUMBER (not VALUE_STRING or VALUE_FLOAT).
      */
     @Test
     public void testTokenBufferNumberTokenType() throws Exception {
         BigDecimal val = new BigDecimal("123.456");
         TokenBuffer tb = new TokenBuffer(mapper, false);
         tb.writeNumber(val);

         JsonParser p = tb.asParser();
         assertEquals("Token must be VALUE_NUMBER", JsonToken.VALUE_NUMBER, p.nextToken());
         assertEquals("BigDecimal must equal", val, p.getDecimalValue());
         p.close();
     }
 }