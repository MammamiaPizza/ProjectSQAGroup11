package com.google.javascript.jscomp;

 /**
  * Tests for {@link CheckSideEffects}.  The buggy version does not correctly
  * report side-effect-free expression statements under some conditions.
  * These tests establish the expected behaviour: that useless expressions
  * produce a JSC_USELESS_CODE warning, while statements with side effects
  * produce no warning.
  */
 public class CheckSideEffectsTest extends CompilerTestCase {

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     enableNormalize(false);
   }

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new CheckSideEffects(compiler, CheckLevel.WARNING,
         /* protectSideEffectFreeCode = */ false);
   }

   // -----------------------------------------------------------------
   // Cases that SHOULD produce a USELESS_CODE_ERROR warning
   // -----------------------------------------------------------------

   public void testUselessCode() {
     // The original trigger test: a simple binary expression statement.
     test("x + 1;", null, CheckSideEffects.USELESS_CODE_ERROR,
         "Suspicious code. The result of the 'add' operator is not being used.");
   }

   public void testPureBinaryExpression() {
     test("a + b;", null, CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testStringLiteralStatement() {
     test("\"hello\";", null, CheckSideEffects.USELESS_CODE_ERROR,
         "Suspicious code. Is there a missing '+' on the previous line?");
   }

   public void testBooleanCompare() {
     test("x == 1;", null, CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testIdentifierExpression() {
     test("x;", null, CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testPureExpressionInBlock() {
     test("{ x + 1; }", null, CheckSideEffects.USELESS_CODE_ERROR);
   }

   // -----------------------------------------------------------------
   // Cases that SHOULD NOT produce a warning
   // -----------------------------------------------------------------

   public void testAssignmentHasSideEffect() {
     testSame("x = 1;");
   }

   public void testCallHasSideEffect() {
     testSame("foo();");
   }

   public void testEmptyStatementNoWarning() {
     testSame(";");
   }

   public void testEmptyBlockNoWarning() {
     testSame("{}");
   }

   public void testVarDeclarationNoWarning() {
     testSame("var x = 1;");
   }

   public void testForLoopNoWarning() {
     // The init and increment expressions are used by the loop; no warning.
     testSame("for(var i = 0; i < 10; i++) { x = i; }");
   }
 }
