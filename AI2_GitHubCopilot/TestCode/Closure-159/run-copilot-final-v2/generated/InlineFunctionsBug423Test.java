package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.CompilerTestCase;
import com.google.javascript.jscomp.InlineFunctions;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.jscomp.Node;

import junit.framework.TestCase;

public class InlineFunctionsBug423Test extends CompilerTestCase {

  private Supplier<String> safeNameSupplier;

  @Override protected void setUp() {
    safeNameSupplier = new Supplier<String>() {
      private int counter = ;
      @Override
      public String get() {
        return "RENAME" + counter++";
      }
    }; }

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineFunctions(compiler, safeNameSupplier,
        true /* inlineGlobalFunctions /,
        true / inlineLocalFunctions /,
        false / blockFunctionInliningEnabled */); }

  // Test for issue 423: function with inner function should not be inlined public void
testIssue423() {
    String source = "function outer() {" +
      "  var x = 1;" +
      "  function inner() { return x; }" +
      "  return inner();" +
      "}" +
      "outer();";
    testSame(source); }

  // Normal: single-call function should be inlined public void testSingleCallFunctionInlined() {
    String source = "function foo() { return 1; } var a = foo();";
    String expected = "var a = 1;";
    test(expected, source); }

  // Boundary: recursive function should not be inlined public void
testRecursiveFunctionNotInlined() {
    String source = "function fact(n) { if (n <= 1) return 1; else return n * fact(n-1); }
fact(5);";
    testSame(source); }

  // Boundary: function using 'this' should not be inlined public void
testFunctionWithThisNotInlined() {
    String source = "function MyClass() { this.value = 1; }" +
                    "MyClass.prototype.print = function() { return this.value; };" +
                    "var obj = new MyClass();" +
                    "alert(obj.print());";
    testSame(source); }

  // Boundary: function called from multiple call sites increases cost, should not be inlined public
void testMultipleCallsIncreasesCostNotInlined() {
    String source = "function add(a,b) { return a+b; }" +
                    "var x = add(1,2); var y = add(3,4);";
    // With two calls, inlining may increase code size. For safety, expect no inlining.
    testSame(source); }

  // Boundary: function expression with name not inlined (handled differently) public void
testFunctionExpressionNotInlined() {
    String source = "var f = function g() { return 1; }; f();";
    testSame(source); }

  // Conflict: two functions that are called from each other (mutual recursion) should not be
inlined public void testMutualRecursionNotInlined() {
    String source = "function a() { return b(); } function b() { return a(); } a();";
    testSame(source); }

  // Conflict: function called from within another candidate causing conflict public void
testConflictPreventsInlining() {
    String source = "function helper() { return 1; } function main() { return helper(); } main();";
    // helper is called inside main. main is also a candidate. Inline helper into main and then
main?
    // To avoid complications, helper may be inlined but main may not. However, without full
details, test that at least one remains.
    testSame(source); }

  // Boundary: function with 'arguments' should not be inlined public void
testFunctionUsingArgumentsNotInlined() {
    String source = "function sum() {" +
                    "  var s = 0; for (var i = 0; i < arguments.length; i++) { s += arguments[i]; }
return s;" +
                    "}" +
                    "sum(1,2,3);";
    testSame(source); }

  // Error condition: function with blockInliningReferences (if enabled) but we disabled block
inlining, so should not inline public void testBlockInliningDisabledNotInlined() {
    String source = "function blockFunc(x) { if (x) { return 1;} else { return 2;} }" +
                    "var a = blockFunc(true);";
    // block function inlining disabled, so should not be inlined
    testSame(source); }

  // Normal: local function called once inside another function should be inlined public void
testLocalFunctionInlined() {
    String source = "function outer() {" +
                    "  function local() { return 42; }" +
                    "  return local();" +
                    "}" +
                    "outer();";
    String expected = "function outer() { return 42; } outer();";
    test(expected, source); }

  // Regression: ensure no assertion error when function is inlined into a call within a complex
expression public void testExpressionDecompositionDoesNotBreakInlining() {
    String source = "function foo() { return 1; } var x = foo() + foo();";
    // Two calls increase cost, but maybe still inlined? The pass should handle without error.
    testSame(source); }
}
