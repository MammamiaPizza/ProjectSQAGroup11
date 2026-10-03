package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.CompilerPass;
 import com.google.javascript.jscomp.CompilerTestCase;
 import com.google.javascript.jscomp.InlineVariaables;
 import com.google.javascript.jscomp.InlineVariables.Mode;

 public class InlineVariaablesTest extends CompilerTestCase {

   public InlineVariablesTest() {
     super("", false);
   }

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     setAcceptedLanguage(CompilerOptions.LanguageMode.ECMASCRIPT5);
     disableNormalize();
     setExterns("function alert(){}; function foo(){}; function bar(){}; var side;");
   }

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new InlineVariables(compiler, Mode.ALL, true);
   }

   /**
    * Variable with side-effect initializer used multiple times should not be inlined.
    * Bug 1053: inlining changes evaluation count.
    */
   public void testExternalIssue1053() {
     testSame(
         "function foo(){window.side=1;return 1}" +
         "var x=foo();" +
         "alert(x);" +
         "alert(x);");
   }

   /**
    * A constant initializer should be inlined.
    */
   public void testInlineConstant() {
     test("var x=1; alert(x);", "alert(1);");
   }

   /**
    * A variable that is later assigned should not be inlined.
    */
   public void testNoInlineWithLValueAssign() {
     testSame("var x=1; x=2; alert(x);");
   }

   /**
    * A variable initialized with a side effect that is used only once can be inlined.
    */
   public void testInlineSideEffectUsedOnce() {
     test("var x=foo(); bar(x);", "bar(foo());");
   }

   /**
    * A variable initialized with a side effect used in both branches of a conditional
    * should not be inlined, because it would cause double evaluation in each branch.
    */
   public void testNoInlineSideEffectInConditional() {
     testSame("var x=foo(); if(cond){bar(x)} else{bar(x)}");
   }

   /**
    * Immutable values (numbers) used multiple times are safe to inline.
    */
   public void testInlineNumberMultipleRefs() {
     test("var n=3; alert(n); alert(n);", "alert(3);alert(3);");
   }

   /**
    * Short strings are worth inlining when all strings are enabled.
    */
   public void testInlineStringShort() {
     test("var s='hi'; alert(s);", "alert('hi');");
   }
 }
