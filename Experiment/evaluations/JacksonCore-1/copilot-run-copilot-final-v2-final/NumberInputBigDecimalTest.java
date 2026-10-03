package com.fasterxml.jackson.core.io;

 import static org.junit.Assert.*;

 import java.math.BigDecimal;

 import org.junit.Test;

 import com.fasterxml.jackson.core.util.BufferRecycler;
 import com.fasterxml.jackson.core.util.TextBuffer;

 public class NumberInputBigDecimalTest {

     @Test
     public void testParseBigDecimalValid() {
         assertEquals(0, NumberInput.parseBigDecimal("0").compareTo(BigDecimal.ZERO));
         assertEquals(new BigDecimal("2.2250738585072012e-308"),
                 NumberInput.parseBigDecimal("2.2250738585072012e-308"));
         assertEquals(BigDecimal.valueOf(1, 0), NumberInput.parseBigDecimal("1"));
         assertEquals(new BigDecimal("1e308"), NumberInput.parseBigDecimal("1e308"));
         assertEquals(0, NumberInput.parseBigDecimal("-0.0").compareTo(BigDecimal.ZERO));
         assertEquals(new BigDecimal(Double.toString(Double.MAX_VALUE)),
                 NumberInput.parseBigDecimal(Double.toString(Double.MAX_VALUE)));
         assertEquals(new BigDecimal(Double.toString(Double.MIN_VALUE)),
                 NumberInput.parseBigDecimal(Double.toString(Double.MIN_VALUE)));
     }

     @Test
     public void testParseBigDecimalNonFiniteString() {
         String[] nonFinite = {"NaN", "Infinity", "+Infinity", "-Infinity", "Inf", "+Inf", "-Inf"};
         for (String s : nonFinite) {
             try {
                 NumberInput.parseBigDecimal(s);
                 fail("Expected NumberFormatException for input: " + s);
             } catch (NumberFormatException e) {
                 assertNotNull("Exception message should not be null for input " + s,
                         e.getMessage());
                 assertTrue("Exception message should contain expected substring for input " + s +
                                 ", but was: " + e.getMessage(),
                         e.getMessage().contains("can not be represented as BigDecimal"));
             }
         }
     }

     @Test
     public void testParseBigDecimalNonFiniteCharArray() {
         String[] nonFinite = {"NaN", "Infinity", "+Infinity", "-Infinity", "Inf", "+Inf", "-Inf"};
         for (String s : nonFinite) {
             char[] buffer = s.toCharArray();
             try {
                 NumberInput.parseBigDecimal(buffer, 0, buffer.length);
                 fail("Expected NumberFormatException for char[] input: " + s);
             } catch (NumberFormatException e) {
                 assertNotNull("Exception message should not be null for input " + s,
                         e.getMessage());
                 assertTrue("Exception message should contain expected substring for input " + s +
                                 ", but was: " + e.getMessage(),
                         e.getMessage().contains("can not be represented as BigDecimal"));
             }
         }
     }

     @Test
     public void testContentsAsDecimalValid() {
         TextBuffer tb = new TextBuffer(null);
         tb.resetWithString("123.456");
         BigDecimal result = tb.contentsAsDecimal();
         assertEquals(new BigDecimal("123.456"), result);
     }

     @Test
     public void testContentsAsDecimalNonFinite() {
         TextBuffer tb = new TextBuffer(null);
         String[] nonFinite = {"NaN", "Infinity", "-Infinity", "Inf", "-Inf"};
         for (String s : nonFinite) {
             tb.resetWithString(s);
             try {
                 tb.contentsAsDecimal();
                 fail("Expected NumberFormatException for TextBuffer contents: " + s);
             } catch (NumberFormatException e) {
                 assertNotNull("Exception message should not be null for input " + s,
                         e.getMessage());
                 assertTrue("Exception message should contain expected substring for input " + s +
                                 ", but was: " + e.getMessage(),
                         e.getMessage().contains("can not be represented as BigDecimal"));
             }
         }
     }

     @Test
     public void testParseBigDecimalNaNOnly() {
         try {
             NumberInput.parseBigDecimal("NaN");
             fail("Expected NumberFormatException");
         } catch (NumberFormatException e) {
             assertNotNull(e.getMessage());
             assertTrue(e.getMessage().contains("can not be represented as BigDecimal"));
         }
     }

     @Test
     public void testParseBigDecimalInfinityVariants() {
         String[] infs = {"Infinity", "+Infinity", "-Infinity", "Inf", "+Inf", "-Inf"};
         for (String s : infs) {
             try {
                 NumberInput.parseBigDecimal(s);
                 fail("Expected NumberFormatException for " + s);
             } catch (NumberFormatException e) {
                 assertNotNull(e.getMessage());
                 assertTrue(e.getMessage().contains("can not be represented as BigDecimal"));
             }
         }
     }

     @Test
     public void testParseBigDecimalZeroRepresentations() {
         assertEquals(0, NumberInput.parseBigDecimal("0").compareTo(BigDecimal.ZERO));
         assertEquals(0, NumberInput.parseBigDecimal("0.0").compareTo(BigDecimal.ZERO));
         assertEquals(0, NumberInput.parseBigDecimal("-0").compareTo(BigDecimal.ZERO));
         assertEquals(0, NumberInput.parseBigDecimal("-0.0").compareTo(BigDecimal.ZERO));
     }

     @Test
     public void testParseBigDecimalLargeAndSmall() {
         assertEquals(new BigDecimal("1e308"), NumberInput.parseBigDecimal("1e308"));
         assertEquals(new BigDecimal("-1e-308"), NumberInput.parseBigDecimal("-1e-308"));
         assertEquals(new BigDecimal("2.2250738585072012e-308"),
                 NumberInput.parseBigDecimal("2.2250738585072012e-308"));
     }
 }
