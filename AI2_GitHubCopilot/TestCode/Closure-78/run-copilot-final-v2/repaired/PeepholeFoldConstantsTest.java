package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests for division-by-zero folding in {@link PeepholeFoldConstants} (Bug #381).
  *
  * <p>The buggy version incorrectly reports a {@code JSC_DIVIDE_BY_0_ERROR} when folding constant
  * division by zero.  According to the JavaScript specification, {@code 1/0} evaluates to
  * {@code Infinity}, {@code 0/0} to {@code NaN}, and {@code -1/0} to {@code -Infinity}.
  * The optimizer must fold these without diagnostic errors.
  */
 public class PeepholeFoldConstantsTest extends TestCase {

     @Override
     protected void setUp() throws Exception {
         super.setUp();
     }

     private void test(String js, String expected) {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         compiler.initOptions(options);
         compiler.compile(
             CommandLineRunner.getDefaultExterns(),
             compiler.parseTestCode(js),
             options);
         Node root = compiler.getRoot();
         CompilerPass pass = new PeepholeOptimizationsPass(compiler, new PeepholeFoldConstants());
         pass.process(null, root);
         assertEquals(
             "Unexpected errors: " + compiler.getErrors(),
             0, compiler.getErrorCount());
         assertEquals(expected, compiler.toSource(root));
     }

     /**
      * {@code 1 / 0} should fold to {@code Infinity} without a diagnostic error.
      */
     public void testFoldDivisionByZeroInfinity() {
         test("var a = 1 / 0;", "var a = Infinity;");
     }

     /**
      * {@code -1 / 0} should fold to {@code -Infinity} without a diagnostic error.
      */
     public void testFoldDivisionByZeroNegativeInfinity() {
         test("var a = -1 / 0;", "var a = -Infinity;");
     }

     /**
      * {@code 0 / 0} should fold to {@code NaN} without a diagnostic error.
      */
     public void testFoldDivisionByZeroNaN() {
         test("var a = 0 / 0;", "var a = NaN;");
     }

     /**
      * {@code 1 % 0} should fold to {@code NaN} without a diagnostic error.
      */
     public void testFoldModuloByZeroNaN() {
         test("var a = 1 % 0;", "var a = NaN;");
     }

     /**
      * Normal constant division should still fold correctly.
      */
     public void testFoldNormalDivision() {
         test("var a = 6 / 3;", "var a = 2;");
     }

     /**
      * Normal constant modulo should still fold correctly.
      */
     public void testFoldNormalModulo() {
         test("var a = 7 % 4;", "var a = 3;");
     }

     /**
      * Normal constant multiplication should still fold correctly.
      */
     public void testFoldNormalMultiplication() {
         test("var a = 5 * 3;", "var a = 15;");
     }

     /**
      * Division by a constant zero with a variable numerator must NOT fold
      * and must NOT emit a diagnostic error.
      */
     public void testDivisionByZeroWithVariableNoFold() {
         test("var a = x / 0;", "var a = x / 0;");
     }
 }
