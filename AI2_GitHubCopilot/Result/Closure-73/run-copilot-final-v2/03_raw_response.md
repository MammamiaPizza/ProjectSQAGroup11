package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;

 /**
  * Tests for CodeGenerator string escaping, focusing on control characters
  * and line terminators that must be escaped per ECMAScript, regardless of
  * the output charset encoding.
  *
  * @see <a href="http://code.google.com/p/closure-compiler/issues/detail?id=416">Bug 416</a>
  */
 public class CodeGeneratorUniodeTest extends TestCase {

   /**
    * Helper to obtain a UTF-8 CharsetEncoder; the bug manifests when the
    * encoder considers a control character encodable.
    */
   private static CharsetEncoder utf8Encoder() {
     return Charset.forName("UTF-8").newEncoder();
   }

   // ----------------------------------------------------------------
   //  Basic safe characters
   // ----------------------------------------------------------------
   public void testJsStringWithNormalChar() {
       String input = "A";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Unexecaped control char", result.contains("\u007f"));
       assertTrue(result.contains("A"));
   }

   // ----------------------------------------------------------------
   //  Control characters that MUST be escaped per JS spec
   // ----------------------------------------------------------------
   public void testEscapeDEL_U007f() {
       String input = "\u007f";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw DEL char present", result.contains("\u007f"));
       assertTrue("Missing \\u007f escape", result.contains("\\u007f"));
   }

   public void testEscapeDEL_U007fWithEncoder() {
       String input = "\u007f";
       String result = CodeGenerator.jsString(input, utf8Encoder());
       assertFalse("Raw DEL char with encoder", result.contains("\u007f"));
       assertTrue("Missing \\u007f escape with encoder", result.contains("\\u007f"));
   }

   public void testEscapeControlChar_U0001() {
       String input = "\u0001";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+0001 present", result.contains("\u0001"));
       assertTrue("Missing \\u0001 escape", result.contains("\\u0001"));
   }

   public void testEscapeControlChar_U001f() {
       String input = "\u001f";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+001F present", result.contains("\u001f"));
       assertTrue("Missing \\u001f escape", result.contains("\\u001f"));
   }

   public void testEscapeControlChar_U0080() {
       String input = "\u0080";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+0080 present", result.contains("\u0080"));
       assertTrue("Missing \\u0080 escape", result.contains("\\u0080"));
   }

   public void testEscapeControlChar_U009f() {
       String input = "\u009f";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+009F present", result.contains("\u009f"));
       assertTrue("Missing \\u009f escape", result.contains("\\u009f"));
   }

   // ----------------------------------------------------------------
   //  Line terminators (U+2028 LINE SEPARATOR, U+2029 PARAGRAPH SEPARATOR)
   // ----------------------------------------------------------------
   public void testEscapeLineSep_U2028() {
       String input = "\u2028";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+2028 present", result.contains("\u2028"));
       assertTrue("Missing \\u2028 escape", result.contains("\\u2028"));
   }

   public void testEscapeLineSep_U2029() {
       String input = "\u2029";
       String result = CodeGenerator.jsString(input, null);
       assertFalse("Raw U+2029 present", result.contains("\u2029"));
       assertTrue("Missing \\u2029 escape", result.contains("\\u2029"));
   }

   // ----------------------------------------------------------------
   //  Escape-to-double-quoted wrapper
   // ----------------------------------------------------------------
   public void testEscapeToDoubleQuotedJsStringRespectsEscaping() {
       String input = "\u007f";
       String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
       assertFalse("Raw DEL in double\u2010quoted result", result.contains("\u007f"));
       assertTrue("Missing escape in double\u2010quoted result", result.contains("\\u007f"));
   }
 }