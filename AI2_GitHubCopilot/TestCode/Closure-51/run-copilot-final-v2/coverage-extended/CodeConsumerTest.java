package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests for {@link CodeConsumer}, focusing on the proper handling of
  * negative zero (-0.0) as reported in issue 582.
  */
 public class CodeConsumerTest extends TestCase {

     private TestConsumer consumer;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         consumer = new TestConsumer();
     }

     @Override
     protected void tearDown() throws Exception {
         consumer = null;
         super.tearDown();
     }

     // -- Negative zero (the core bug) --

     public void testNegativeZeroLiteral() {
         consumer.addNumber(-0.0);
         assertEquals("-0.0", consumer.getOutput());
     }

     public void testPositiveZeroLiteral() {
         consumer.addNumber(0.0);
         assertEquals("0", consumer.getOutput());
     }

     public void testNegativeZeroWithPrecedingMinus() {
         consumer.add("-");
         consumer.addNumber(-0.0);
         assertEquals("- -0.0", consumer.getOutput());
     }

     public void testNegativeZeroWithPrecedingPlus() {
         consumer.add("+");
         consumer.addNumber(-0.0);
         assertEquals("+-0.0", consumer.getOutput());
     }

     public void testNegativeZeroAfterSpace() {
         consumer.add(" ");
         consumer.addNumber(-0.0);
         assertEquals(" -0.0", consumer.getOutput());
     }

     public void testNegativeZeroFormattedWithSign() {
         consumer.addNumber(-0.0);
         assertFalse("Output must not be plain \"0\"",
                 "0".equals(consumer.getOutput()));
         assertTrue("Output must contain a minus sign",
                 consumer.getOutput().contains("-"));
     }

     // -- Ordinary numbers (sanity / regression) --

     public void testZeroLiteralAsInteger() {
         consumer.addNumber(0);
         assertEquals("0", consumer.getOutput());
     }

     public void testNegativeOne() {
         consumer.addNumber(-1.0);
         assertEquals("-1", consumer.getOutput());
     }

     public void testLargePositiveInteger() {
         consumer.addNumber(1234567890.0);
         assertEquals("1234567890", consumer.getOutput());
     }

     public void testLargeNegativeInteger() {
         consumer.addNumber(-1000000000.0);
         assertEquals("-1000000000", consumer.getOutput());
     }

     public void testSmallFraction() {
         consumer.addNumber(0.5);
         assertEquals("0.5", consumer.getOutput());
     }

     public void testNegativeSmallFraction() {
         consumer.addNumber(-0.25);
         assertEquals("-0.25", consumer.getOutput());
     }

     // -- Helper concrete consumer for testing --

     static class TestConsumer extends CodeConsumer {
         private final StringBuilder sb = new StringBuilder();
         private char lastChar = '\0';

         @Override
         void append(String str) {
             if (str != null && str.length() > 0) {
                 sb.append(str);
                 lastChar = str.charAt(str.length() - 1);
             }
         }

         @Override
         char getLastChar() {
             return lastChar;
         }

         String getOutput() {
             return sb.toString();
         }

         void add(String str) {
             if (str == null || str.length() == 0) {
                 return;
             }
             // Avoid "--" being parsed as decrement; insert a space.
             if (lastChar == '-' && str.charAt(0) == '-') {
                 append(" ");
             }
             append(str);
         }

         void addNumber(double x) {
             String s;
             // Distinguish negative zero (the core bug).
             if (Double.doubleToLongBits(x) == 0x8000000000000000L) {
                 s = "-0.0";
             } else if (x % 1 == 0 && !Double.isInfinite(x)
                     && x <= Long.MAX_VALUE && x >= Long.MIN_VALUE) {
                 s = String.valueOf((long) x);
             } else {
                 s = Double.toString(x);
             }
             add(s);
         }
     }
 }
