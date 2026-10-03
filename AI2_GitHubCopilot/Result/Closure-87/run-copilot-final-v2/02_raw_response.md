package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerPass;
 import com.google.javascript.jscomp.CompilerTestCase;

 /**
  * Tests for {@link PeepholeSubstituteAlternateSyntax}.
  * This test class targets the bug described in issue 291, where folding
  * EXPR_RESULT blocks in event‑handler‑like functions could change return‑value
  * semantics in Internet Explorer. Additional tests exercise regular‑expression
  * constructor folding and literal‑constructor folding.
  */
 public class PeepholeSubstituteAlternateSyntaxTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new PeepholeSubstituteAlternateSyntax(compiler);
   }

   // ---------------------------------------------------------------------------
   // Issue 291 – do NOT fold a simple if‑return‑else‑return into a conditional
   // return, because the function may be used as an IE event handler.
   // ---------------------------------------------------------------------------
   public void testIssue291() {
     testSame("function f() { if (a) return 1; else return 2; }");
   }

   // ---------------------------------------------------------------------------
   // Regular‑expression constructor folding
   // ---------------------------------------------------------------------------
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

   // ---------------------------------------------------------------------------
   // Forward‑slash in source – makeForwardSlashBracketSafe must escape it
   // ---------------------------------------------------------------------------
   public void testRegExpConstructorWithForwardSlash() {
     test("new RegExp('a/b','g')", "/a\\/b/g");
   }

   // ---------------------------------------------------------------------------
   // Source containing a Unicode‑escape pattern – folding must be blocked
   // (containsUnicodeEscape returns true → areSafeFlagsToFold returns false)
   // ---------------------------------------------------------------------------
   public void testRegExpConstructorWithUnicodeEscape() {
     testSame("new RegExp('\\\\u0041','g')");
   }

   // ---------------------------------------------------------------------------
   // Invalid flags – folding must not happen
   // ---------------------------------------------------------------------------
   public void testRegExpConstructorInvalidFlags() {
     testSame("new RegExp('abc','x')");
   }

   // ---------------------------------------------------------------------------
   // Literal‑constructor folding (e.g. new Array)
   // ---------------------------------------------------------------------------
   public void testFoldArrayConstructor_empty() {
     test("new Array()", "[]");
   }

   public void testFoldArrayConstructor_nonEmpty() {
     test("new Array(1,2,3)", "[1,2,3]");
   }

   // ---------------------------------------------------------------------------
   // Standard‑constructor folding (e.g. new String, new Boolean, new Number)
   // ---------------------------------------------------------------------------
   public void testFoldStandardConstructor_string() {
     test("new String('hello')", "'hello'");
   }

   public void testFoldStandardConstructor_boolean() {
     test("new Boolean(true)", "!0");
   }
 }