package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import com.google.javascript.rhino.Node;

 /**
  * Tests for {@link PeepholeSubstituteAlternateSyntax}.
  */
 public class PeepholeSubstituteAlternateSyntaxTest extends TestCase {

   public void testIssue291() {
     testSame("function f() { if (a) return 1; else return 2; }");
   }

   public void testRegExpConstructorFolding_gFlag() {
     test("new RegExp('abc','g')", "/abc/g");
   }

   public void testRegExpConstructorFolding_mFlag() {
     test("new RegExp('abc','m')", "/abc/m");
   }

   public void testRegExpConstructorFolding_iFlag() {
     test("new RegExp('abc','i')", "/abc/i");
   }

   public void testRegExpConstructorFolding_combinedFlags() {
     test("new RegExp('abc','gim')", "/abc/gim");
   }

   public void testRegExpConstructorFolding_emptyFlags() {
     test("new RegExp('abc','')", "/abc/");
   }

   public void testRegExpConstructorWithForwardSlash() {
     test("new RegExp('a/b','g')", "/a\\/b/g");
   }

   public void testRegExpConstructorWithUnicodeEscape() {
     testSame("new RegExp('\\\\u0041','g')");
   }

   public void testRegExpConstructorInvalidFlags() {
     testSame("new RegExp('abc','x')");
   }

   public void testFoldArrayConstructor_empty() {
     test("new Array()", "[]");
   }

   public void testFoldArrayConstructor_nonEmpty() {
     test("new Array(1,2,3)", "[1,2,3]");
   }

   public void testFoldStandardConstructor_string() {
     test("new String('hello')", "'hello'");
   }

   public void testFoldStandardConstructor_boolean() {
     test("new Boolean(true)", "!0");
   }

   private void testSame(String js) {
     test(js, js);
   }

   private void test(String js, String expected) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);
     Node script = compiler.parseTestCode(js);
     if (script == null) {
       fail("Failed to parse: " + js);
     }
     PeepholeSubstituteAlternateSyntax pass = new PeepholeSubstituteAlternateSyntax(compiler);
     pass.process(null, script);
     String result = compiler.toSource(script);
     assertEquals(expected, result);
   }
 }
