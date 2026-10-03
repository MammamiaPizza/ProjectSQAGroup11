package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CompilerTestCase;

 /**
  * Tests for {@link PeepholeSubstituteAlternateSyntax#tryFoldSimpleFunctionCall} focusing on
  * the {@code String()} folding introduced around bug 759.
  */
 public class PeepholeSubstituteAlternateSyntaxTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new PeepholeSubstituteAlternateSyntax(false);
   }

   /**
    * A call with a single literal number argument should be folded.
    */
   @Test
   public void testStringLiteralNumber() {
     test("String(42)", "''+42");
   }

   /**
    * A call with a single literal string argument should be folded.
    */
   @Test
   public void testStringLiteralString() {
     test("String('hello')", "''+'hello'");
   }

   /**
    * A call with a single literal boolean argument should be folded.
    */
   @Test
   public void testStringLiteralBoolean() {
     test("String(true)", "''+true");
   }

   /**
    * A call with a single literal null argument should be folded.
    */
   @Test
   public void testStringLiteralNull() {
     test("String(null)", "''+null");
   }

   /**
    * A call with a variable argument must NOT be folded in the general case
    * (issue 759).
    */
   @Test
   public void testStringVariableNotFolded() {
     testSame("String(x)");
   }

   /**
    * A call with a property-access argument must NOT be folded because the
    * argument is not a simple immutable literal.
    */
   @Test
   public void testStringGetPropNotFolded() {
     testSame("String(obj.prop)");
   }

   /**
    * A call with a function-call argument must NOT be folded; the function
    * may have side-effects and the semantics could change.
    */
   @Test
   public void testStringCallArgNotFolded() {
     testSame("String(f())");
   }

   /**
    * A call with the first argument being {@code this} must NOT be folded
    * (it is not an immutable literal).
    */
   @Test
   public void testStringThisNotFolded() {
     testSame("String(this)");
   }

   /**
    * A call with {@code undefined} (identifier, not a literal) must NOT be
    * folded.
    */
   @Test
   public void testStringUndefinedNotFolded() {
     testSame("String(undefined)");
   }

   /**
    * A call with multiple arguments must NOT be folded because the extra
    * arguments could contain side-effects that must be preserved.
    */
   @Test
   public void testStringMultipleArgsNotFolded() {
     testSame("String(a, b++)");
   }

   /**
    * A call with a single literal empty string argument should be folded.
    */
   @Test
   public void testStringEmptyLiteral() {
     test("String('')", "''+''");
   }

   /**
    * A call with a complex but literal expression (e.g., escaped string) should
    * still be folded because it is an immutable literal.
    */
   @Test
   public void testStringLiteralWithEscape() {
     test("String('\\\\n')", "''+'\\\\n'");
   }
 }