package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests for {@link CodeGenerator} focusing on Bug 942:
  * numeric string keys in object literals must be printed as
  * number literals (e.g. [0] not ["0"]).
  */
 public class CodeGeneratorTest extends TestCase {

     // ---- isSimpleNumber (core of Bug 942) ----

     public void testIsSimpleNumber_zero() {
         // Bug 942: "0" should be recognized as a simple number
         assertTrue("'0' must be a simple number",
                    CodeGenerator.isSimpleNumber("0"));
     }

     public void testIsSimpleNumber_positiveDigits() {
         assertTrue(CodeGenerator.isSimpleNumber("1"));
         assertTrue(CodeGenerator.isSimpleNumber("9"));
         assertTrue(CodeGenerator.isSimpleNumber("10"));
         assertTrue(CodeGenerator.isSimpleNumber("42"));
         assertTrue(CodeGenerator.isSimpleNumber("999999"));
     }

     public void testIsSimpleNumber_leadingZeros() {
         assertFalse("'00' must not be simple (octal ambiguity)",
                     CodeGenerator.isSimpleNumber("00"));
         assertFalse(CodeGenerator.isSimpleNumber("01"));
         assertFalse(CodeGenerator.isSimpleNumber("0010"));
     }

     public void testIsSimpleNumber_empty() {
         assertFalse("empty string must not be simple",
                     CodeGenerator.isSimpleNumber(""));
     }

     public void testIsSimpleNumber_nonNumeric() {
         assertFalse(CodeGenerator.isSimpleNumber("abc"));
         assertFalse(CodeGenerator.isSimpleNumber("0x0"));
         assertFalse(CodeGenerator.isSimpleNumber("1e2"));
         assertFalse(CodeGenerator.isSimpleNumber("1.5"));
         assertFalse(CodeGenerator.isSimpleNumber("-1"));
         assertFalse(CodeGenerator.isSimpleNumber(" 0"));
     }

     // ---- getSimpleNumber ----

     public void testGetSimpleNumber_zero() {
         assertEquals(0.0, CodeGenerator.getSimpleNumber("0"), 0.0);
     }

     public void testGetSimpleNumber_positiveValues() {
         assertEquals(1.0, CodeGenerator.getSimpleNumber("1"), 0.0);
         assertEquals(42.0, CodeGenerator.getSimpleNumber("42"), 0.0);
     }

     public void testGetSimpleNumber_invalidReturnsNaN() {
         assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("abc")));
         assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("00")));
         assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("")));
         assertTrue(Double.isNaN(CodeGenerator.getSimpleNumber("1.5")));
     }

     // ---- Object-literal key integration ----

     public void testObjectLiteralKeyZero_noQuotes() {
         // Bug 942 core case
         testOutput("var x={0:1}", "var x={0:1}");
     }

     public void testObjectLiteralKeyMultiDigit_noQuotes() {
         testOutput("var x={42:1,100:2}", "var x={42:1,100:2}");
     }

     public void testObjectLiteralKeyString_keepsQuotes() {
         testOutput("var x={\"abc\":1}", "var x={\"abc\":1}");
     }

     public void testObjectLiteralKeyLeadingZero_keepsQuotes() {
         testOutput("var x={\"01\":1}", "var x={\"01\":1}");
     }

     // ---- helper ----

     private void testOutput(String input, String expected) {
         Compiler compiler = new Compiler();
         compiler.disableThreads();
         CompilerOptions options = new CompilerOptions();
         JSSourceFile[] inputs = {JSSourceFile.fromCode("in.js", input)};
         JSSourceFile[] externs = {JSSourceFile.fromCode("ex.js", "")};
         Result r = compiler.compile(externs, inputs, options);
         assertTrue("Compilation failed: " + r.errors, r.success);
         assertEquals(expected, compiler.toSource().trim());
     }
 }