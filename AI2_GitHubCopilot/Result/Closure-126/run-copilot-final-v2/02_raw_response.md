<tool_calls>
<invoke name="bash">
<parameter name="command" value="cat <<'EOF'
package com.google.javascript.jscomp;

import org.junit.Test;
import junit.framework.TestCase;

public class MinimizeExitPointsTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new MinimizeExitPoints(compiler); }

  @Override protected void setUp() throws Exception {
    super.setUp();
    setAcceptedLanguage(CompilerOptions.LanguageMode.ECMASCRIPT3); }

  @Test public void testBreakInTryFinallyNotRemoved() {
    // break inside try with finally must remain
    test("while(true){try{if(a)break;}finally{foo();}}",
         "while(true){try{if(a)break;}finally{foo();}}"); }

  @Test public void testReturnRemovedAtEndOfFunction() {
    // empty return at end of function is redundant
    test("function f(){return;}",
         "function f(){}"); }

  @Test public void testReturnNotRemovedInTryFinally() {
    // return inside try with finally must remain
    test("function f(){try{return;}finally{foo();}}",
         "function f(){try{return;}finally{foo();}}"); }

  @Test public void testReturnWithValueNotRemoved() {
    // return with a value should not be treated as minimisable exit
    test("function f(){return 1;}",
         "function f(){return 1;}"); }

  @Test public void testBreakInLoopRemoved() {
    // break at the very end of a loop body is redundant
    test("while(true){break;}",
         "while(true){}"); }

  @Test public void testContinueInForLoopOptimized() {
    // continue inside if should trigger sibling reordering
    test("for(var i=0;i<10;i++){if(i==5)continue;alert(i)}",
         "for(var i=0;i<10;i++){if(i==5);else{alert(i)}}"); }

  @Test public void testLabeledBreakOptimized() {
    // labeled break matched against a label should be minimized
    test("L:while(true){if(x){break L;}foo();}",
         "L:while(true){if(x);else{foo();}}"); }

  @Test public void testMultipleReturnsOptimized() {
    // multiple empty returns in a function get transformed to nested if-else
    test("function f(a,b){if(a)return;if(b)return;bar();}",
         "function f(a,b){if(a);else{if(b);else{bar();}}}"); }

  @Test public void testNestedFunctionReturnsOptimized() {
    // nested function returns are independently optimized
    test("function f(){function g(){return;}g();return;}",
         "function f(){function g(){}g();}"); }

  @Test public void testDontRemoveBreakInTryFinally() {
    // original failing test: break inside labelled try-finally must stay
    test("function f(){L:while(true){try{if(x)break L;}finally{dosomething();}}}",
         "function f(){L:while(true){try{if(x)break L;}finally{dosomething();}}}"); }

  @Test public void testFunctionReturnOptimization() {
    // original failing test: basic if-return optimization
    test("function f(){if(x)return;else foo();}",
         "function f(){if(x);else{foo();}}"); }

  @Test public void testBreakInFinallyNotRemoved() {
    // break inside a finally block must never be removed
    test("while(true){try{}finally{break;}}",
         "while(true){try{}finally{break;}}"); }
}
EOF
</parameter>
</invoke>
</tool_calls>