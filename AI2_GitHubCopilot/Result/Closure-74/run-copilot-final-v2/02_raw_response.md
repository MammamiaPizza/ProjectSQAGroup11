package com.google.javascript.jscomp;

public class PeepholeFoldConstantsTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(compiler, new PeepholeFoldConstants()); }

  @Override protected int getNumRepetitions() {
    return 1; }

  // Basic numeric comparisons public void testFoldComparison1() {
    test("x = 5 > 3;", "x = true;");
    test("x = 5 < 3;", "x = false;");
    test("x = 5 >= 5;", "x = true;");
    test("x = 5 <= 5;", "x = true;");
    test("x = 5 <= 3;", "x = false;");
    test("x = 5 == 5;", "x = true;");
    test("x = 5 != 3;", "x = true;");
    test("x = 5 != 5;", "x = false;"); }

  // Comparisons with 0 and -0 (they are equal under == but not under strict equality) public void
testFoldComparisonZero() {
    test("x = 0 > -0;", "x = false;");
    test("x = -0 > 0;", "x = false;");
    test("x = 0 < -0;", "x = false;");
    test("x = 0 >= -0;", "x = true;");
    test("x = 0 <= -0;", "x = true;");
    test("x = 0 == -0;", "x = true;");
    test("x = 0 === 0;", "x = true;");  // ensure identity not mistakenly broken }

  // Comparisons with NaN – all relational and equality should be false, inequality true public void
testFoldComparisonWithNaN() {
    test("x = NaN > 0;", "x = false;");
    test("x = NaN < 0;", "x = false;");
    test("x = NaN >= 0;", "x = false;");
    test("x = NaN <= 0;", "x = false;");
    test("x = NaN ==0;", "x = false;");
    test("x = NaN !=0;", "x = true;");

 // operand order reversed
 test("x = 0 > NaN;", "x = false;");
 test("x = 0 < NaN;", "x = false;");
 test("x = 0 >= NaN;", "x = false;");
 test("x = 0 <= NaN;", "x = false;");
 test("x = 0 == NaN;", "x = false;");
 test("x = 0 != NaN;", "x = true;");

 // NaN compared to itself
 test("x = NaN == NaN;", "x = false;");
 test("x = NaN != NaN;", "x = true;"); }

  // Commutative operators: a > b must fold identically to b < a public void
testCommutativeOperators() {
    test("x = 3 > 5;", "x = false;");
    test("x = 5 < 3;", "x = false;");
    test("x = 3 > 5;", "x = false;"); // explicit pair

 test("x = 2 >= 5;", "x = false;");
 test("x = 5 <= 2;", "x = false;");

 test("x = 2 == 5;", "x = false;");
 test("x = 5 == 2;", "x = false;");

 test("x =2 != 5;", "x = true;");
 test("x =5 != 2;", "x = true;");

 // numeric string comparison – should be commutative after ToNumber conversion
 test("x = 3 > '2';", "x = true;");
 test("x = '2' < 3;", "x = true;"); }

  // Invertible operators: !(a >= b) must fold to a < b public void testInvertibleOperators() {
    test("x = !(5 >= 3);", "x = false;"); // because 5 >=3 -> true, !true -> false
    test("x = !(3 > 5);", "x = true;");   // 3 >5 -> false, !false -> true
    test("x = !(5 <= 3);", "x = true;");  // 5<=3 -> false, !false -> true
    test("x = !(3 < 5);", "x = false;");  // 3<5 -> true, !true -> false
    test("x = !(4 == 4);", "x = false;");
    test("x = !(4 != 4);", "x = true;");

 // with NaN: !(NaN > 0) should be true because NaN>0 is false
 test("x = !(NaN > 0);", "x = true;"); }

  // Comparisons involving undefined public void testFoldComparisonWithUndefined() {
    // undefined > any number is false; undefined < number is false; undefined == number is false
    test("x = void 0 > 0;", "x = false;");
    test("x = void 0 < 0;", "x = false;");
    test("x = void 0 == 0;", "x = false;");
    test("x = void 0 != 0;", "x = true;");
    test("x = void 0 >= 0;", "x = false;");
    test("x = void 0 <= 0;", "x = false;");

 // reversed operand
 test("x = 0 > void 0;", "x = false;");
 test("x = 0 < void 0;", "x = false;");
 test("x = 0 == void 0;", "x = false;");
 test("x = 0 != void 0;", "x = true;");

 // undefined == undefined should be true
 test("x = void 0 == void 0;", "x = true;");
 test("x = void 0 != void 0;", "x = false;");

 // undefined == null is true
 test("x = void 0 == null;", "x = true;");
 test("x = void 0 != null;", "x = false;"); }

  // Mixed types: boolean vs number public void testFoldComparisonMixedTypes() {
    // true converts to 1, false to 0
    test("x = true > 0;", "x = true;");
    test("x = false > 0;", "x = false;");
    test("x = true == 1;", "x = true;");
    test("x = false == 0;", "x = true;");
    test("x = true == 0;", "x = false;");
    test("x = false != 0;", "x = false;"); }

  // Edge case: string "undefined" (literal) is just a string, not the value undefined public void
testFoldComparisonWithStringUndefined() {
    test("x = 'undefined' > 0;", "x = false;"); // string 'undefined' converted to number => NaN,
NaN > 0 => false
    test("x = 'undefined' == 0;", "x = false;");
    test("x = 'undefined' != 0;", "x = true;");
    // avoid folding with non-numeric string? }

  // Ensure folding does not happen when operands are not both constants public void
testNoFoldWhenNonConstant() {
    testSame("function f() { return 5 > 3; }"); // should not fold inside a function? Actually may
inline but we test that it doesn't crash
    testSame("x = a > 3;");
    testSame("x = 3 > a;"); }
}
}