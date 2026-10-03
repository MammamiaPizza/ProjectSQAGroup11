package com.google.javascript.jscomp;

 /**
  * Self-contained JUnit3 test for {@link CheckSideEffects} focusing on the
  * missing warning described in Bug 753. Tests validate that side‑effect‑free
  * expressions (literals, comma children, and especially a string that becomes
  * a standalone expression when a concatenation + is omitted) trigger the
  * expected USELESS_CODE_ERROR, while used expressions and intentional semi‑
  * colons do not.
  */
 public class CheckSideEffectsBugTest extends CompilerTestCase {

   private final CheckLevel checkLevel;
   private final boolean protectSideEffectFreeCode;

   public CheckSideEffectsBugTest() {
     this.checkLevel = CheckLevel.WARNING;
     this.protectSideEffectFreeCode = false;
   }

   @Override
   protected CompilerPass getProcessor(final Compiler compiler) {
     return new CheckSideEffects(compiler, checkLevel, protectSideEffectFreeCode);
   }

   @Override
   protected int getNumIterations() {
     return 1;
   }

   @Override
   protected void setUp() throws Exception {
     super.setUp();
   }

   // ---------- normal useless expressions ---------

   public void testNumberLiteral() {
     testError("42;", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testStringLiteral() {
     testError("'hello';", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testBooleanLiteral() {
     testError("true;", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testNullLiteral() {
     testError("null;", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testObjectLiteral() {
     testError("({});", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testArrayLiteral() {
     testError("([]);", CheckSideEffects.USELESS_CODE_ERROR);
   }

   // --------- Bug 753: missing '+' in string concatenation ---------

   /**
    * When a '+' is omitted between two string literals, ASI turns the second
    * literal into a standalone expression statement, which must elicit a
    * “missing +” warning.
    */
   public void testMissingPlus() {
     testError("var x = 'a'\n'b'", CheckSideEffects.USELESS_CODE_ERROR);
   }

   // --------- boundary / branch coverage ---------

   public void testUselessCodeInComma() {
     testError("1, 'a';", CheckSideEffects.USELESS_CODE_ERROR);
   }

   public void testNoWarningForUsedExpression() {
     testSame("var x = foo();");
   }

   public void testNoWarningForVoidSemicolon() {
     testSame(";");
   }

   public void testNoWarningForAssignment() {
     testSame("x = 1;");
   }

   public void testNoWarningForExprResult() {
     testError("'foo'", CheckSideEffects.USELESS_CODE_ERROR);
   }
 }