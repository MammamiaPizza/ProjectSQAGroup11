package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;

/**

 - JUnit tests for the FlowSensitiveInlineVariables pass, targeting the bug
 - described in issue 698.
 -
 - This test class validates that variables are inlined only when it is safe to
 - do so, i.e. across calls that have no side effects, and not across calls or
 - property modifications that could affect the variable's value.
 -
 - The expected behaviour is derived from the public contract of the compiler
 - pass: a single‑use variable may be inlined when there are no side‑effects
 - along any path from its definition to its use and no redefinitions.
  */
 public class FlowSensitiveInlineVariablesBugTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler); }

  @Override protected void setUp() throws Exception {
    super.setUp();
    super.enableNormalize();
    super.enableComputeSideEffects();
    setAcceptedLanguage(CompilerOptions.LanguageMode.ECMASCRIPT5); }

  /**

 - Inlining must NOT happen across a call that has side effects.
 - Here {@code alert(x)} is a side‑effectful call that comes between the
 - definition and the use.
    */
   public void testInlineAcrossSideEffect1() {
 testSame(
    "function f() { var x = compute(); alert(x); return x; }");
   }

  /**

 - Inlining should happen across a call that has no side effects.
 - {@code Math.abs(0)} is a pure function, therefore the variable {@code x}
 - can be safely inlined after it.
    */
   public void testCanInlineAcrossNoSideEffect() {
 test(
    "function f() { var x = 1; var y = Math.abs(0); return x; }",
    "function f() { var y = Math.abs(0); return 1; }");
   }

  /**

 - Reproduces a minimal snippet extracted from issue 698.
 - The variable {@code x} is defined from a property of {@code a} and then
 - {@code a} is reassigned. Because the property access may now refer to a
 - different object, inlining must NOT happen.
    */
   public void testIssue698() {
 testSame(
    "var a = {}; function f() { var x = a.prop; a = {}; alert(x); }");
   }

  /**

 - When a property write happens between the definition and the use, the
 - variable must not be inlined.
    */
   public void testNoInlineAcrossPropertyWrite() {
 testSame(
    "var obj = {p:1}; function f() { var x = obj.p; obj.p = 2; return x; }");
   }

  /**

 - A variable that is redefined inside a loop cannot be inlined.
    */
   public void testNoInlineInLoop() {
 testSame(
    "function f() { var x = 0; while(true) { x = x + 1; } return x; }");
   }

  /**

 - A variable with multiple definitions via a conditional (if/else) must not
 - be inlined.
    */
   public void testNoInlineAfterConditional() {
 testSame(
    "function f() { var x; if (c) { x = 1; } else { x = 2; } return x; }");
   }

  /**

 - The simplest safe inlining case – a single definition followed by a single
 - use with no intervening statements.
    */
   public void testSimpleInline() {
 test(
    "function f() { var x = 1; return x; }",
    "function f() { return 1; }");
   }

  /**

 - Another safe scenario: the interleaving call has no side effects
 - (String length access).
    */
   public void testInlineAcrossNoSideEffect2() {
 test(
    "function f() { var x = 1; var y = 'hello'.length; return x; }",
    "function f() { var y = 'hello'.length; return 1; }");
   }

  /**

 - Multiple uses of the same definition inside different branches should be
 - inlined when each use sees the same definition and there are no side
 - effects.
    */
   public void testInlineMultipleUsesSameDef() {
 test(
    "function f() { var x = 1; if (c) { alert(x); } else { alert(x); } }",
    "function f() { var x = 1; if (c) { alert(1); } else { alert(1); } }");
   }

  /**

 - Even when the variable is aliased via a function call that can modify the
 - underlying object, inlining must not happen across that call.
    */
   public void testNoInlineAcrossSideEffectWithAliasing() {
 testSame(
    "var obj = {}; function f() { var x = obj.prop; someFunc(obj); alert(x); }");
   }

  /**

 - A variable that is redefined before its use must not be inlined in any of
 - its earlier uses (the pass should conservatively not inline).
    */
   public void testNoInlineAfterRedefinition() {
 testSame(
    "function f() { var x = 1; x = 2; alert(x); }");
   }

  /**

 - A variable defined in an outer scope and used in a nested function is not
 - a candidate for this pass (it must not be inlined because it lives through
 - a function boundary).
    */
   public void testNoInlineAcrossFunctionBoundary() {
 testSame(
    "function f() { var x = 1; function g() { return x; } return g; }");
   }
 }
