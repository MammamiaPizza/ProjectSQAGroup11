package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class CodeConsumerTest extends TestCase {

   // Concrete minimal CodeConsumer that captures appended output
   private static final class TestCodeConsumer extends CodeConsumer {
     private final StringBuilder buf = new StringBuilder();
     private char lastChar = '\0';

     @Override
     char getLastChar() {
       if (buf.length() == 0) {
         return lastChar;
       }
       return buf.charAt(buf.length() - 1);
     }

     @Override
     void append(String str) {
       buf.append(str);
     }

     void setLastChar(char c) {
       this.lastChar = c;
     }

     String getOutput() {
       return buf.toString();
     }

     void reset() {
       buf.setLength(0);
       lastChar = '\0';
     }
   }

   public void testIsNegativeZero() {
     assertTrue(CodeConsumer.isNegativeZero(-0.0));
     assertFalse(CodeConsumer.isNegativeZero(0.0));
     assertFalse(CodeConsumer.isNegativeZero(1.0));
     assertFalse(CodeConsumer.isNegativeZero(-1.0));
     assertFalse(CodeConsumer.isNegativeZero(Double.NaN));
     assertFalse(CodeConsumer.isNegativeZero(Double.NEGATIVE_INFINITY));
     assertFalse(CodeConsumer.isNegativeZero(Double.POSITIVE_INFINITY));
   }

   public void testAddNumberPositive() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar(' ');
     c.addNumber(42);
     assertEquals("42", c.getOutput());
   }

   public void testAddNumberNegative() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar(' ');
     c.addNumber(-7);
     assertEquals("-7", c.getOutput());
   }

   public void testAddNumberNegativeAfterMinusInsertsSpace() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('-');
     c.addNumber(-4);
     assertEquals(" -4", c.getOutput());
   }

   public void testAddNumberNegativeZeroAfterMinusShouldInsertSpace() {
     // This is the core bug: x- -0.0 must not become x--0.0
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('-');
     c.addNumber(-0.0);
     assertEquals(" -0.0", c.getOutput());
   }

   public void testAddNumberNegativeZeroWithoutMinusNoSpace() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('x');
     c.addNumber(-0.0);
     assertEquals("-0.0", c.getOutput());
   }

   public void testAddNumberPositiveZeroAfterMinusNoSpace() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('-');
     c.addNumber(0.0);
     assertEquals("0", c.getOutput());
   }

   public void testAddNumberVerySmallNegativeNoSpace() {
     // e.g. -Double.MIN_VALUE, which is negative but not zero
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('x');
     c.addNumber(-Double.MIN_VALUE);
     String out = c.getOutput();
     assertTrue(out.startsWith("-"));
     // no leading space
     assertEquals('-', out.charAt(0));
   }

   public void testAddNumberLargeUsesExponential() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar(' ');
     c.addNumber(1000000000);
     // 1E9 is acceptable compaction; only assert that exponential notation
     // is used for very large round numbers
     assertTrue(c.getOutput().contains("E"));
   }

   public void testAddNumberInfinity() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('-');
     c.addNumber(Double.NEGATIVE_INFINITY);
     assertEquals(" -Infinity", c.getOutput());

     c.reset();
     c.setLastChar(' ');
     c.addNumber(Double.POSITIVE_INFINITY);
     assertEquals("Infinity", c.getOutput());
   }

   public void testAddNumberNaN() {
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar('-');
     c.addNumber(Double.NaN);
     // NaN is not <0 and not negativeZero, so no extra space
     assertEquals("NaN", c.getOutput());
   }

   public void testSubtractionChainWithNegativeZero() {
     // Simulate code like: x - (-0.0) - y
     TestCodeConsumer c = new TestCodeConsumer();
     c.setLastChar(' ');
     c.addIdentifier("x");
     assertEquals("x", c.getOutput());

     c.reset();
     c.setLastChar('-');
     c.addNumber(-0.0);
     assertEquals(" -0.0", c.getOutput()); // space inserted

     c.reset();
     c.setLastChar(' ');
     // after -0.0, another subtraction
     c.addOp("-", true);
     c.addIdentifier("y");
     // just assure no crash; output verification optional
     assertTrue(c.getOutput().contains("y"));
   }
 }
