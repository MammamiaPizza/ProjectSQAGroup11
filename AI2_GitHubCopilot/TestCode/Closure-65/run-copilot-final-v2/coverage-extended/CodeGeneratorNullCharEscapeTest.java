package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests for {@link CodeGenerator#escapeToDoubleQuotedJsString(String)}
  * with focus on correct escaping of the null character (bug 65 / issue 486).
  * The null character must be emitted as octal \000, not as the short form \0.
  */
 public class CodeGeneratorNullCharEscapeTest extends TestCase {

     private static final char NULL_CHAR = '\0';

     // ---- null character alone ----
     public void testNullCharOnly() {
         String result = CodeGenerator.escapeToDoubleQuotedJsString(
                 Character.toString(NULL_CHAR));
         // expected: a double-quoted string containing the three-digit octal escape
         assertEquals("\"\\000\"", result);
     }

     // ---- null followed by a decimal digit ----
     public void testNullFollowedByDigit() {
         // The digit must not become part of the octal escape
         String input = NULL_CHAR + "0";
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"\\0000\"", result);
     }

     // ---- null at start of string ----
     public void testNullAtStart() {
         String input = NULL_CHAR + "abc";
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"\\000abc\"", result);
     }

     // ---- null at end of string ----
     public void testNullAtEnd() {
         String input = "xyz" + NULL_CHAR;
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"xyz\\000\"", result);
     }

     // ---- null in the middle ----
     public void testNullInMid() {
         String input = "ab" + NULL_CHAR + "cd";
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"ab\\000cd\"", result);
     }

     // ---- multiple consecutive nulls ----
     public void testMultipleNulls() {
         String input = "" + NULL_CHAR + NULL_CHAR + NULL_CHAR;
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"\\000\\000\\000\"", result);
     }

     // ---- verify that the short form \0 never appears ----
     public void testNoShortFormNullEscape() {
         String input = NULL_CHAR + "test" + NULL_CHAR + "end";
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         // The short form "\0" (backslash + zero) must not be present at all
         assertFalse("Short null escape \\0 found but \\000 expected",
                 result.contains("\\0") && !result.contains("\\00"));
     }

     // ---- null with surrounding special characters ----
     public void testNullWithSpecialChars() {
         // linefeed, null, carriage-return, backslash
         String input = "\n" + NULL_CHAR + "\r\\";
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertTrue("Expected \\n\\000\\r\\\\", result.contains("\\n\\000\\r\\\\"));
     }

     // ---- ordinary strings are unchanged ----
     public void testOrdinaryString() {
         assertEquals("\"hello\"",
                 CodeGenerator.escapeToDoubleQuotedJsString("hello"));
     }

     // ---- empty string ----
     public void testEmptyString() {
         assertEquals("\"\"",
                 CodeGenerator.escapeToDoubleQuotedJsString(""));
     }

     // ---- double-quote inside is escaped ----
     public void testDoubleQuoteInside() {
         assertEquals("\"a\\\"b\"",
                 CodeGenerator.escapeToDoubleQuotedJsString("a\"b"));
     }

     // ---- a real-world short string with null and digits ----
     public void testMixedNullAndDigits() {
         String input = NULL_CHAR + "1" + NULL_CHAR + "2" + NULL_CHAR;
         String result = CodeGenerator.escapeToDoubleQuotedJsString(input);
         assertEquals("\"\\0001\\0002\\000\"", result);
     }
 }
