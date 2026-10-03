package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests the numeric-literal detection used when CodeGenerator serializes
  * numeric object-literal keys (bug 569).
  */
 public class CodeGeneratorTest extends TestCase {

   public void testSimpleNumberRejectsSingleZero() {
     assertFalse(CodeGenerator.isSimpleNumber("0"));
   }

   public void testSimpleNumberRecognizesPlainInteger() {
     assertTrue(CodeGenerator.isSimpleNumber("1"));
     assertTrue(CodeGenerator.isSimpleNumber("10"));
     assertTrue(CodeGenerator.isSimpleNumber("123456789"));
   }

   public void testSimpleNumberRejectsNonDigitCharacters() {
     assertFalse(CodeGenerator.isSimpleNumber(""));
     assertFalse(CodeGenerator.isSimpleNumber("-1"));
     assertFalse(CodeGenerator.isSimpleNumber("1.0"));
     assertFalse(CodeGenerator.isSimpleNumber("1e2"));
     assertFalse(CodeGenerator.isSimpleNumber("0xA"));
     assertFalse(CodeGenerator.isSimpleNumber("Infinity"));
     assertFalse(CodeGenerator.isSimpleNumber("NaN"));
     assertFalse(CodeGenerator.isSimpleNumber(" 1"));
   }

   public void testSimpleNumberRejectsLeadingZeros() {
     assertFalse(CodeGenerator.isSimpleNumber("010"));
     assertFalse(CodeGenerator.isSimpleNumber("00"));
     assertFalse(CodeGenerator.isSimpleNumber("0001"));
   }

   public void testGetSimpleNumberParsesPlainInteger() {
     assertEquals(1.0, CodeGenerator.getSimpleNumber("1"), 0.0);
     assertEquals(10.0, CodeGenerator.getSimpleNumber("10"), 0.0);
     assertEquals(123456789.0, CodeGenerator.getSimpleNumber("123456789"), 0.0);
   }

   public void testGetSimpleNumberReturnsNaNForLeadingZeros() {
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("010")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("00")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("0")));
   }

   public void testGetSimpleNumberReturnsNaNForNonNumericStrings() {
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("0xA")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("1.0")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("1.00")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("1e2")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("Infinity")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("NaN")));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("")));
   }

   public void testGetSimpleNumberReturnsNaNWhenLongParseOverflows() {
     assertTrue(CodeGenerator.isSimpleNumber("9223372036854775808"));
     assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("9223372036854775808")));
   }
 }
