package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.jscomp.CompilerTestCase;

/**

 - Tests for label renaming in {@link FunctionToBlockMutator#mutate} and
 - related {@link RenameLabels} pass.
 -
 - <p>Bug #435: when a function is inlined whose body contains a label that
 - collides with an outer label, the inner label must be renamed so that
 - break/continue statements still target the correct label.
  */
 public class FunctionToBlockMutatorTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineFunctions(compiler); }

  /** Label conflict: inner function has same label as outer scope. */ public void
testLabelConflictRenamed() {
    test(
        "function f() { a: while(1) { break a; } }" +
        "function g() { a: while(1) { f(); break a; } }" +
        "g()",
        "function g() {" +
        "  JSCompiler_inline_label_f_0:" +
        "    while(1) { break JSCompiler_inline_label_f_0; } " +
        "  a: while(1) { break a; }" +
        "}" +
        "g()"); }

  /** Break inside inlined body must target the renamed label. */ public void
testBreakToRenamedLabel() {
    test(
        "function h() { L: { break L; } }" +
        "function outer() { L: { h(); break L; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_h_0: { break JSCompiler_inline_label_h_0; } " +
        "  L: { break L; }" +
        "}" +
        "outer()"); }

  /** Continue inside inlined loop must target renamed label. */ public void
testContinueInInlinedLoop() {
    test(
        "function loop() { L: while(1) { continue L; } }" +
        "function outer() { L: while(1) { loop(); continue L; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_loop_0:" +
        "    while(1) { continue JSCompiler_inline_label_loop_0; } " +
        "  L: while(1) { continue L; }" +
        "}" +
        "outer()"); }

  /** Nested labeled blocks inside inlined function. */ public void testNestedLabelsInlined() {
    test(
        "function n() { A: { B: { break A; } } }" +
        "function outer() { A: { n(); break A; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_n_0:" +
        "    JSCompiler_inline_label_n_1: { break JSCompiler_inline_label_n_0; } " +
        "  A: { break A; }" +
        "}" +
        "outer()"); }

  /** No label conflict – labels are still made unique. */ public void testNoLabelConflict() {
    test(
        "function unique() { X: while(0) { break X; } }" +
        "function outer() { Y: { unique(); break Y; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_unique_0:" +
        "    while(0) { break JSCompiler_inline_label_unique_0; } " +
        "  Y: { break Y; }" +
        "}" +
        "outer()"); }

  /** Unreferenced label in inlined function may be removed. */ public void
testUnusedLabelRemovedFromInlined() {
    test(
        "function un() { X: { 1; } }" +
        "function outer() { L: { un(); break L; } }" +
        "outer()",
        "function outer() {" +
        "  { 1; }" +
        "  L: { break L; }" +
        "}" +
        "outer()"); }

  /** Multiple inlined functions with same label each get unique names. */ public void
testMultipleFunctionsSameLabel() {
    test(
        "function f1() { L: { break L; } }" +
        "function f2() { L: { break L; } }" +
        "function caller() { A: { f1(); f2(); break A; } }" +
        "caller()",
        "function caller() {" +
        "  JSCompiler_inline_label_f1_0: { break JSCompiler_inline_label_f1_0; } " +
        "  JSCompiler_inline_label_f2_0: { break JSCompiler_inline_label_f2_0; } " +
        "  A: { break A; }" +
        "}" +
        "caller()"); }

  /** Inlining into a labeled scope with same label name. */ public void
testInlineIntoLabeledScope() {
    test(
        "function body() { L: while(1) { break L; } }" +
        "function outer() { L: { body(); break L; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_body_0:" +
        "    while(1) { break JSCompiler_inline_label_body_0; } " +
        "  L: { break L; }" +
        "}" +
        "outer()"); }

  /** Inlined function has a label inside a loop body. */ public void testLoopBodyLabel() {
    test(
        "function loopBody() { L: for(;;) { break L; } }" +
        "function outer() { while(1) { M: { loopBody(); break M; } } }" +
        "outer()",
        "function outer() {" +
        "  while(1) {" +
        "    M: {" +
        "      JSCompiler_inline_label_loopBody_0:" +
        "        for(;;) { break JSCompiler_inline_label_loopBody_0; } " +
        "      break M;" +
        "    }" +
        "  }" +
        "}" +
        "outer()"); }

  /** Label conflict when function has arguments; renaming still applies. */ public void
testLabelConflictWithArguments() {
    test(
        "function arg(x) { L: while(x) { break L; } return L; }" +
        "function outer() { L: while(1) { arg(1); break L; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_arg_0:" +
        "    while(1) { break JSCompiler_inline_label_arg_0; } " +
        "  L: while(1) { break L; }" +
        "}" +
        "outer()"); }

  /** Inlining a function with no labels works normally. */ public void testNoLabelsInlined() {
    test(
        "function noLabel() { return 1; }" +
        "function outer() { return noLabel(); }" +
        "outer()",
        "function outer() { return 1; }" +
        "outer()"); }

  /** Break from label inside inlined body nested deeper. */ public void testDeepBreakRenamed() {
    test(
        "function deep() { A: { B: { break A; } } }" +
        "function outer() { A: { deep(); break A; } }" +
        "outer()",
        "function outer() {" +
        "  JSCompiler_inline_label_deep_0:" +
        "    JSCompiler_inline_label_deep_1: { break JSCompiler_inline_label_deep_0; } " +
        "  A: { break A; }" +
        "}" +
        "outer()"); }
}