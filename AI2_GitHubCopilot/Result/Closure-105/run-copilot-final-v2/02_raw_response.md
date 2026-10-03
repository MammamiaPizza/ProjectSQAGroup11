package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 public class FoldConstantsTest extends CompilerTestCase {

   public FoldConstantsTest() {
     super("", true);
   }

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new FoldConstants(compiler);
   }

   @Override
   protected int getNumRepetitions() {
     return 1;
   }

   /**
    * Basic two-literal string concatenation should fold.
    */
   public void testSimpleStringConcat() {
     test("var x = 'a' + 'b'", "var x = \"ab\"");
   }

   /**
    * Three-literal string join via left-child ADD folding.
    * This is the primary failing case for bug 105.
    * "a" + "b" + "c" should fold to "abc".
    */
   public void testStringJoinAddThreeLiterals() {
     test("var x = 'a' + 'b' + 'c'", "var x = \"abc\"");
   }

   /**
    * Four-literal chain exercises deeper left-child folding.
    */
   public void testStringJoinAddFourLiterals() {
     test("var x = 'a' + 'b' + 'c' + 'd'", "var x = \"abcd\"");
   }

   /**
    * Five-literal chain pushes the folder further.
    */
   public void testStringJoinAddFiveLiterals() {
     test("var x = 'a' + 'b' + 'c' + 'd' + 'e'", "var x = \"abcde\"");
   }

   /**
    * Empty string concatenation.
    */
   public void testEmptyStringConcat() {
     test("var x = '' + ''", "var x = \"\"");
   }

   /**
    * String plus empty string should fold.
    */
   public void testStringWithEmptyConcat() {
     test("var x = 'a' + ''", "var x = \"a\"");
     test("var x = '' + 'a'", "var x = \"a\"");
   }

   /**
    * String concatenation inside a return statement.
    */
   public void testStringConcatInReturn() {
     test("function f() { return 'a' + 'b' + 'c'; }",
          "function f() { return \"abc\"; }");
   }

   /**
    * No folding when a non-literal variable is involved.
    */
   public void testStringConcatWithVarNoFold() {
     testSame("var x = 'a' + y");
     testSame("var x = y + 'b'");
     testSame("var x = y + 'a' + 'b'");
   }

   /**
    * String plus number literal: number coerced to string, should fold.
    */
   public void testStringPlusNumberConcat() {
     test("var x = 'a' + 1 + 'b'", "var x = \"a1b\"");
   }

   /**
    * Number first then string: the chain starts with a number literal.
    * Since NodeUtil.getStringValue for a number may return null in the
    * left position, the folding behavior for the full chain may vary,
    * but a trailing string literal pair should still fold.
    */
   public void testNumberPlusStringChain() {
     test("var x = 1 + 'a' + 'b'", "var x = \"1ab\"");
   }

   /**
    * String join used as a function argument.
    */
   public void testStringConcatAsArgument() {
     test("foo('a' + 'b' + 'c')", "foo(\"abc\")");
   }

   /**
    * String join across multiple statements to ensure independence.
    */
   public void testMultipleStringConcats() {
     test("var x = 'h' + 'i'; var y = 'b' + 'y' + 'e'",
          "var x = \"hi\"; var y = \"bye\"");
   }
 }