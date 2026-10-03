package com.google.javascript.jscomp;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.Normalize;
import com.google.javascript.jscomp.CompilerPass;

public class NormalizeBug139Test extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new Normalize(compiler, false); }

  public void testSplitVarDeclarations() {
    // Multiple var declarations should be split into individual var statements.
    test("var a=1,b=2;", "var a=1;var b=2;"); }

  public void testSingleVarNoSplit() {
    // Single var declaration remains unchanged.
    testSame("var a=1;"); }

  public void testRemoveDuplicateVarInGlobal() {
    // Duplicate var declaration should be converted to assignment.
    test("var a=1;var a=2;", "var a=1;a=2;"); }

  public void testRemoveDuplicateVarInFunction() {
    // Function-scoped duplicate var should also be removed.
    test("function f(){var a=1;var a=2;}", "function f(){var a=1;a=2;}"); }

  public void testNoDuplicateInNestedScopes() {
    // Same-name var in different scopes is not a duplicate.
    testSame("var a=1;function f(){var a=2;}"); }

  public void testMoveFunctionDeclarations() {
    // Function declarations should be hoisted to the top of the enclosing function body.
    test(
        "function f(){var a=1;function g(){}return a;}",
        "function f(){function g(){}var a=1;return a;}"); }

  public void testMoveMultipleFunctionDeclarations() {
    // Multiple functions hoisted, preserving relative order.
    test(
        "function f(){var x=1;function a(){}var y=2;function b(){}return;}",
        "function f(){function a(){}function b(){}var x=1;var y=2;return;}"); }

  public void testNormalizeLabels() {
    // Label with a non-block body should be wrapped in a block.
    test("a:if(x);", "a:{if(x);}"); }

  public void testExtractForInitializerVar() {
    // FOR initializer (VAR) should be extracted before the loop.
    test(
        "for(var i=0;i<10;i++){}",
        "var i=0;for(;i<10;i++){}"); }

  public void testExtractForInitializerExpression() {
    // Expression initializer extracted as expression statement.
    test(
        "for(i=0;i<10;i++){}",
        "i=0;for(;i<10;i++){}"); }

  public void testDuplicateVarErrorRemoved() {
    // After normalization, no JSC_VAR_MULTIPLY_DECLARED_ERROR should remain.
    testNoWarning("var x=1;var x=2;"); }

  public void testMoveFunctionInsideIf() {
    // Function declaration inside a block (if) should be hoisted to enclosing function top.
    test(
        "function f(){if(true){var x=1;function g(){}}}",
        "function f(){function g(){}if(true){var x=1;}}"); }
}
